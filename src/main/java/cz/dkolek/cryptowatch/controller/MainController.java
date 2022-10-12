package cz.dkolek.cryptowatch.controller;

import cz.dkolek.cryptowatch.dto.CoinApiDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;

@Controller
public class MainController {

   @GetMapping("/index")
   public String index() {
      return "index";
   }

   @GetMapping(value = "/apiTest")
   public void apiTest() throws URISyntaxException {
      RestTemplate restTemplate = new RestTemplate();

      final String url = "https://rest.coinapi.io/v1/exchangerate/BTC/USD";
      URI uri = new URI(url);

      HttpHeaders headers = new HttpHeaders();
      headers.set("X-CoinAPI-Key", "A949D348-58A4-4D25-ABDF-34E871FFE30D");

      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

      ResponseEntity<String> response = restTemplate.exchange(
              url, HttpMethod.GET, requestEntity, String.class);
      System.out.println(response);
   }
}
