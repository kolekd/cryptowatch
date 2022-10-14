package cz.dkolek.cryptowatch.service;

import cz.dkolek.cryptowatch.dto.DisplayDTO;
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

   private static final String ERHOST_BASEURL = "https://api.exchangerate.host";
   private static final String ERHOST_URL_TIMESERIES = "/timeseries";
   private static final String DEFAULT_BASE_CURRENCY = "BTC";
   private static final String DEFAULT_CONVERSION_CURRENCY = "EUR";

   private static final String PARAM_START_DATE = "start_date";
   private static final String PARAM_END_DATE = "end_date";
   private static final String PARAM_BASE = "base";
   private static final String PARAM_SYMBOLS = "symbols";

   // TODO: Mock data used for testing to avoid getting locked out of API
//   public DisplayDTO getMockData(String currency) {
//      return new DisplayDTO(List.of(List.of("2022-10-07", 20013.54),
//              List.of("2022-10-08", 20014.58),
//              List.of("2022-10-09", 20020.0),
//              List.of("2022-10-10", 19615.529412),
//              List.of("2022-10-11", 19625.54902),
//              List.of("2022-10-12", 19615.941176),
//              List.of("2022-10-13", 19614.372549)), currency);
//   }

   public DisplayDTO callApi(String currency) {
      RestTemplate restTemplate = new RestTemplate();

      final String url = ERHOST_BASEURL + ERHOST_URL_TIMESERIES;
      HttpEntity<?> entity = new HttpEntity<>(new HttpHeaders());

      String startDate = LocalDateTime.now().minusDays(6).format(dateFormatterRequest);
      String endDate = LocalDateTime.now().format(dateFormatterRequest);
      if (currency == null) currency = DEFAULT_CONVERSION_CURRENCY;

      String urlTemplate = UriComponentsBuilder.fromHttpUrl(url)
              .queryParam(PARAM_START_DATE, "{" + PARAM_START_DATE + "}")
              .queryParam(PARAM_END_DATE, "{" + PARAM_END_DATE + "}")
              .queryParam(PARAM_BASE, "{" + PARAM_BASE + "}")
              .queryParam(PARAM_SYMBOLS, "{" + PARAM_SYMBOLS + "}")
              .encode().toUriString();

      Map<String, String> params = new HashMap<>();
      params.put(PARAM_START_DATE, startDate);
      params.put(PARAM_END_DATE, endDate);
      params.put(PARAM_BASE, DEFAULT_BASE_CURRENCY);
      params.put(PARAM_SYMBOLS, currency);

      log.debug("MainService.callApi - Sending request to " + urlTemplate);
      ResponseEntity<ERHostResponseDTO> response = restTemplate.exchange(
              urlTemplate, HttpMethod.GET, entity, ERHostResponseDTO.class, params);

      if (response.getBody() != null) {
         log.debug("MainService.callApi - Request successful, processing" + urlTemplate);
         return processAPIResponse(response.getBody(), currency);
      } else {
         log.error("Response body is null. Response: " + response);
         throw new RuntimeException("Response body is null. Response: " + response);
      }
   }

   private DisplayDTO processAPIResponse(ERHostResponseDTO responseDTO, String currency) {
      List<List<Object>> ret = new ArrayList<>();
      Map<String, Map<String, Double>> rateMap = responseDTO.getRates();
      rateMap.forEach((key, value) -> ret.add(List.of(key, value.get(currency))));
      return new DisplayDTO(ret, currency);
   }
}
