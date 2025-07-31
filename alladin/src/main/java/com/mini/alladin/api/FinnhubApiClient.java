package com.mini.alladin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FinnhubApiClient {

    @Value("${finnhub.api.key}")
    private String apiKey;

    public double getLivePrice(String stockSymbol){
        if(stockSymbol==null || stockSymbol.isEmpty()){
            System.err.println("Stock Symbol is empty");
            return -1;
        }

        String url = "https://finnhub.io/api/v1/quote?symbol="+stockSymbol+"&token="+apiKey;

        try{

            // Use RestTemplate to make HTTP request and get raw response body as plain String as result
            RestTemplate restTemplate = new RestTemplate();
            // Save the raw response as String
            String response = restTemplate.getForObject(url, String.class);

            // ObjectMapper -> Jackson library -> Converts between JSON and JAVA Objects
            ObjectMapper objectMapper = new ObjectMapper();
            // Response is a plain String. As JSON is also a String . Read the Plain String with objectMapper and
            // Convert Plain JSON String to JsonNode
            JsonNode jsonNode = objectMapper.readTree(response);
            // Extract required value from JsonNode
            double currentPrice = jsonNode.path("c").asDouble();
            System.out.println("-------------");
            System.out.println("Live price for : "+stockSymbol+ " from API: "+currentPrice);
            System.out.println("-------------");

            return currentPrice;



        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

}
