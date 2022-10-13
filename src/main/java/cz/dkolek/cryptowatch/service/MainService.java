package cz.dkolek.cryptowatch.service;

import cz.dkolek.cryptowatch.dto.ERHostResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class MainService {

   private static final DateTimeFormatter dateFormatterRequest = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   public static final String ERHOST_BASEURL = "https://api.exchangerate.host";
   public static final String ERHOST_URL_TIMESERIES = "/timeseries";

   public List<List<Object>> getMockData() {
      return List.of(List.of("2022-10-07", 20013.54),
              List.of("2022-10-08", 20014.58),
              List.of("2022-10-09", 20020.0),
              List.of("2022-10-10", 19615.529412),
              List.of("2022-10-11", 19625.54902),
              List.of("2022-10-12", 19615.941176),
              List.of("2022-10-13", 19614.372549));
   }

   public List<List<Object>> callApi() {
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
         return processAPIResponse(response.getBody());
      } else {
         log.error("response body is null");
         throw new RuntimeException("response body is null");
      }
   }

   private List<List<Object>> processAPIResponse(ERHostResponseDTO responseDTO) {
      List<List<Object>> ret = new ArrayList<>();
      String currency = "EUR";
      Map<String, Map<String, Double>> rateMap = responseDTO.getRates();
      rateMap.forEach((key, value) -> ret.add(List.of(key, value.get(currency))));
      return ret;
   }
}
