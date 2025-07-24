package com.mini.alladin.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.springframework.web.util.InvalidUrlException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 *  From Finnhub API : GET : /quote?symbol=AAPL
 *  -> basic HTTP GET request with 2 parameters : symbol and token(although not shown required for authorization)
 *  -> In java we need a way to Build a URL with query parameters and send a GET request
 *  -> So build URL with UriComponentsBuilder with query parameter
 *  -> To send GET request: use RestTemplate
 *  -> Sending getRequest returns JSON
 *  -> Parse the JSON string in Java using JSONObject
 * */
@Component
public class FinnhubApiClientExperimentalJSON {

    @Value("${finnhub.api.key}")
    private String apiKey;

    // Base URL to connect to API (from Finnhub API)
    private final String BASE_URL = "https://finnhub.io/api/v1/quote";

    public double getCurrentPriceJson(String symbol) {

        if(symbol == null || symbol.isEmpty()){
            System.err.println("Symbol cannot be null or empty :)");
            return 0.0;
        }
        try {
            // Build a url as our API needs query based url with symbol and token for authorization
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                    .queryParam("symbol",symbol)
                    .queryParam("token", apiKey)
                    .toUriString();

            // Instead of creating base url and then build query param url we could have easily done:
            // String url = "https://finnhub.io/api/v1/quote?symbol="+symbol+"&token="+apiKey;


            /*RestTemplate because we want to send a HTTP GET request to the url and get a response
            Rest Template handles HTTP GET/POST
            We send HTTP request using RestTemplate and it returns response as String*/
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            // We change the string response in JSON format
            JSONObject jsonObject = new JSONObject(response);
            double currentPrice  = jsonObject.getDouble("c");
            return currentPrice;
        } catch (InvalidUrlException e) {
            throw new RuntimeException(e);
        } catch (RestClientException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }catch(Exception e){
            System.out.println("------------------");
            System.err.println("Error Fetching live price for: " + symbol);
            System.out.println("------------------");
            return 0.0;
        }
    }




}
