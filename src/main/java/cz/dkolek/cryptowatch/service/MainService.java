package cz.dkolek.cryptowatch.service;

import cz.dkolek.cryptowatch.enums.ConversionCurrencyEnum;
import cz.dkolek.cryptowatch.dto.DisplayDTO;
import cz.dkolek.cryptowatch.dto.APIResponse;
import cz.dkolek.cryptowatch.exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class MainService {

   private final MessageSource messageSource;

   public MainService(MessageSource messageSource) {
      this.messageSource = messageSource;
   }

   private static final DateTimeFormatter requestDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

   private static final String ERHOST_URL = "https://api.exchangerate.host/timeseries";
   private static final String DEFAULT_BASE_CURRENCY = "BTC";
   private static final ConversionCurrencyEnum DEFAULT_CONVERSION_CURRENCY = ConversionCurrencyEnum.EUR;

   private static final String PARAM_START_DATE = "start_date";
   private static final String PARAM_END_DATE = "end_date";
   private static final String PARAM_BASE = "base";
   private static final String PARAM_SYMBOLS = "symbols";

   public DisplayDTO getExchangeRates(ConversionCurrencyEnum currencyEnum) throws APIException {
      String currency = Objects.requireNonNullElse(currencyEnum, DEFAULT_CONVERSION_CURRENCY).toString();
      APIResponse response = callAPI(currency);
      return buildDisplayDTOFromResponse(response, currency);
   }

   private APIResponse callAPI(String currency) throws APIException {
      String startDate = LocalDateTime.now().minusDays(6).format(requestDateFormatter);
      String endDate = LocalDateTime.now().format(requestDateFormatter);

      String url = UriComponentsBuilder.fromHttpUrl(ERHOST_URL)
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

      log.info("MainService.getExchangeRates - Sending request to " + url);
      ResponseEntity<APIResponse> response = new RestTemplate().exchange(
              url,
              HttpMethod.GET,
              new HttpEntity<>(new HttpHeaders()),
              APIResponse.class,
              params
      );

      if (!response.getStatusCode().equals(HttpStatus.OK)) {
         String errorMsg = messageSource.getMessage("error.api.request_unsuccessful", new Object[]{ url , response.getStatusCode(), response }, Locale.ENGLISH);
         log.error("MainService.getExchangeRates - " + errorMsg);
         throw new APIException(errorMsg);
      }

      if (response.getBody() == null) {
         String errorMsg = messageSource.getMessage("error.api.response_body_null", new Object[]{ response }, Locale.ENGLISH);
         log.error("MainService.getExchangeRates - " + errorMsg);
         throw new APIException(errorMsg);
      }

      log.info("MainService.getExchangeRates - Request successful");
      return response.getBody();
   }

   private DisplayDTO buildDisplayDTOFromResponse(APIResponse responseDTO, String currency) {
      log.info("MainService.buildDisplayDTOFromResponse - Building display object from response");
      List<List<Object>> ret = new ArrayList<>();
      Map<String, Map<String, Double>> rateMap = responseDTO.getRates();
      rateMap.forEach((key, value) -> ret.add(List.of(key, value.get(currency))));
      return new DisplayDTO(ret, currency);
   }
}
