package cz.dkolek.cryptowatch.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class MainController {

   @GetMapping("/index")
   public String index(Model model) {
      String data = apiCall();
      model.addAttribute("data", data);
      return "index";
   }

   private String apiCall() {
      RestTemplate restTemplate = new RestTemplate();

      final String url = "https://rest.coinapi.io/v1/exchangerate/BTC/USD";
      HttpHeaders headers = new HttpHeaders();
      headers.set("X-CoinAPI-Key", "A949D348-58A4-4D25-ABDF-34E871FFE30D");
      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

      ResponseEntity<String> response = restTemplate.exchange(
              url, HttpMethod.GET, requestEntity, String.class);
      return response.getBody();
   }
}
