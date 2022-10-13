package cz.dkolek.cryptowatch.controller;

import cz.dkolek.cryptowatch.dto.DisplayDTO;
import cz.dkolek.cryptowatch.dto.ERHostResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class MainController {

   public static final String COINAPI_ACCESS_KEY = "A949D348-58A4-4D25-ABDF-34E871FFE30D";
   public static final String COINAPI_BASEURL = "https://rest.coinapi.io";
   public static final String COINAPI_URL_BTC_EUR = "/v1/exchangerate/BTC/EUR/20200523";

   public static final String COINLAYER_ACCESS_KEY = "bbee798694c017f9ee9c9b7fbdac0d28";
   public static final String COINLAYER_BASEURL = "http://api.coinlayer.com/";
   public static final String COINLAYER_URL_TIME_LIVE = "/live";
   public static final String COINLAYER_URL_TIME_TIMEFRAME = "/timeframe";

   private static final DateTimeFormatter dateFormatterRequest = DateTimeFormatter.ofPattern("yyyy-MM-dd");
   private static final DateTimeFormatter dateFormatterDisplay = DateTimeFormatter.ofPattern("MM/dd/yyyy");

   public static final String ERHOST_BASEURL = "https://api.exchangerate.host";
   public static final String ERHOST_URL_TIMESERIES = "/timeseries";

   @GetMapping("/index")
   public String index(Model model) {
      List<DisplayDTO> data = apiCallERHost();
      model.addAttribute("data", data);
      return "index";
   }

   private List<DisplayDTO> apiCallERHost() {
      RestTemplate restTemplate = new RestTemplate();

      final String url = ERHOST_BASEURL + ERHOST_URL_TIMESERIES;
      HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

      String startDate = LocalDateTime.now().minusDays(6).format(dateFormatterRequest);
      String endDate = LocalDateTime.now().format(dateFormatterRequest);

      String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
              .queryParam("start_date", "{start_date}")
              .queryParam("end_date", "{end_date}")
              .queryParam("base", "{base}")
              .queryParam("symbols", "{symbols}")
              .encode().toUriString();

      Map<String, String> params = new HashMap<>();
      params.put("start_date", startDate);
      params.put("end_date", endDate);
      params.put("base", "BTC");
      params.put("symbols", "EUR");

      ResponseEntity<ERHostResponseDTO> response = restTemplate.exchange(
              urlTemplate, HttpMethod.GET, entity, ERHostResponseDTO.class, params);

      if (response.getBody() != null) {
         return processERHostResponse(response.getBody());
      } else {
         log.error("response body is null");
         throw new RuntimeException("response body is null");
      }
   }

   private List<DisplayDTO> processERHostResponse(ERHostResponseDTO responseDTO) {
      List<DisplayDTO> ret = new ArrayList<>();
      String currency = "EUR";
      Map<String, Map<String, Double>> rateMap = responseDTO.getRates();
      rateMap.forEach((key, value) -> ret.add(new DisplayDTO(key, currency, value.get(currency))));
      return ret;
   }

//   private DisplayDTO apiCallCoinLayer() {
//      RestTemplate restTemplate = new RestTemplate();
//
//      final String url = COINLAYER_BASEURL + COINLAYER_URL_TIME_TIMEFRAME;
//      HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());
//
//      String startDate = LocalDateTime.now().format(dateFormatterRequest);
//      String endDate = LocalDateTime.now().minusWeeks(1).format(dateFormatterRequest);
//
//      String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
//              .queryParam("access_key", "{access_key}")
//              .queryParam("start_date", "{start_date}")
//              .queryParam("end_date", "{end_date}")
//              .queryParam("symbols", "{symbols}")
//              .encode().toUriString();
//
//      Map<String, String> params = new HashMap<>();
//      params.put("access_key", COINLAYER_ACCESS_KEY);
//      params.put("start_date", startDate);
//      params.put("end_date", endDate);
//      params.put("symbols", "BTC");
//
//      ResponseEntity<CoinLayerResponseDTO> response = restTemplate.exchange(
//              urlTemplate, HttpMethod.GET, entity, CoinLayerResponseDTO.class, params);
//
//      if (response.getBody() != null) {
//         return processCoinLayerResponse(response.getBody());
//      } else {
//         log.error("response body is null");
//         throw new RuntimeException("response body is null");
//      }
//   }

//   private DisplayDTO processCoinLayerResponse(CoinLayerResponseDTO responseDTO) {
//      String currency = "BTC";
//      Map<String, Map<String, Double>> rateMap = responseDTO.getRates();
////      rateMap.entrySet().stream().forEach();
////      String formattedTimestamp = responseDTO.getTimestamp().format(dateFormatterDisplay);
//      return new DisplayDTO(null, currency, null);
//   }

//   private CoinApiDTO apiCallCoinApi() {
//      RestTemplate restTemplate = new RestTemplate();
//
//      final String url = COINAPI_BASEURL + COINAPI_URL_BTC_EUR;
//      HttpHeaders headers = new HttpHeaders();
//      headers.set("X-CoinAPI-Key", COINAPI_ACCESS_KEY);
//      HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//
//      ResponseEntity<CoinApiDTO> response = restTemplate.exchange(
//              url, HttpMethod.GET, requestEntity, CoinApiDTO.class);
//      CoinApiDTO responseDTO = null;
//      if (response.getBody() != null) {
//         responseDTO = response.getBody();
//         responseDTO.setRate(responseDTO.getRate().setScale(2, RoundingMode.HALF_EVEN));
//      }
//      return responseDTO;
//   }
}
