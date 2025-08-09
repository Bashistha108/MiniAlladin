
package com.mini.alladin.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mini.alladin.entity.Stock;
import com.mini.alladin.repository.StockRepository;
import com.mini.alladin.service.StockPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FinnhubApiClient {

    private final StockRepository stockRepository;

    @Autowired
    public FinnhubApiClient(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Value("${finnhub.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Cache to store last fetched price per stock symbol
    private final Map<String, Double> cache = new ConcurrentHashMap<>();

    // Track when API rate limit was hit (epoch seconds)
    private Instant rateLimitResetTime = Instant.EPOCH;

    private double getPriceFromCacheOrDb(String stockSymbol) {
        Double cachedPrice = cache.get(stockSymbol);
        if (cachedPrice != null) {
            System.out.println("Using cached price for " + stockSymbol);
            return cachedPrice;
        } else {
            Stock stock = stockRepository.findBySymbol(stockSymbol).orElseThrow(()->new RuntimeException("stock not found"));

            BigDecimal currentPrice = stock.getCurrentPrice();
            if(currentPrice != null){
                double dbPrice = stock.getCurrentPrice().doubleValue();
                System.out.println("Using DB price for " + stockSymbol);
                return dbPrice;
            } else {
                System.out.println("No cached or DB price found, returning -1.");
                return -1;
            }
        }
    }


    public double getLivePrice(String stockSymbol) {
        if (stockSymbol == null || stockSymbol.isEmpty()) {
            System.err.println("Stock Symbol is empty");
            return -1;
        }

        Instant now = Instant.now();

        // If we are still in cooldown period after rate limit, return cached value if available
        if (now.isBefore(rateLimitResetTime)) {
            Double cachedPrice = cache.get(stockSymbol);
            System.out.println("Using cached price for " + stockSymbol + " due to API rate limit.");
            return getPriceFromCacheOrDb(stockSymbol);
        }

        String url = "https://finnhub.io/api/v1/quote?symbol=" + stockSymbol + "&token=" + apiKey;

        try {
            // Use exchange to get full response including status code
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);

            if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                // Hit rate limit: set cooldown timer and return cached data
                rateLimitResetTime = now.plusSeconds(60);
                Double cachedPrice = cache.get(stockSymbol);
                System.out.println("API rate limit hit, waiting 1 minute. Using cached price.");
                return getPriceFromCacheOrDb(stockSymbol);
            }

            String responseBody = response.getBody();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            double currentPrice = jsonNode.path("c").asDouble();

            // Cache the fresh price
            cache.put(stockSymbol, currentPrice);

            System.out.println("-------------");
            System.out.println("Live price for : " + stockSymbol + " from API: " + currentPrice);
            System.out.println("-------------");

            return currentPrice;

        } catch (HttpClientErrorException.TooManyRequests e) {
            // In case 429 is thrown as exception instead of status code
            rateLimitResetTime = now.plusSeconds(61);
            Double cachedPrice = cache.get(stockSymbol);
            System.out.println("API rate limit exception caught, waiting 1 minute. Using cached price.");
            return getPriceFromCacheOrDb(stockSymbol);
        } catch (Exception e) {
            // Other exceptions, fallback to cached price if available
            Double cachedPrice = cache.get(stockSymbol);
            System.err.println("Error fetching price from API: " + e.getMessage());
            System.out.println("Using cached price for " + stockSymbol);
            return getPriceFromCacheOrDb(stockSymbol);
        }
    }
}

