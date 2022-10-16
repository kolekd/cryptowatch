package cz.dkolek.cryptowatch.controller;

import cz.dkolek.cryptowatch.enums.ConversionCurrencyEnum;
import cz.dkolek.cryptowatch.dto.DisplayDTO;
import cz.dkolek.cryptowatch.exception.APIException;
import cz.dkolek.cryptowatch.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class MainController {

   private final MainService mainService;

   @Autowired
   public MainController(MainService mainService) {
      this.mainService = mainService;
   }

   @GetMapping("/index")
   public String index(@RequestParam(value = "currency", required = false) ConversionCurrencyEnum currency, Model model) throws APIException {
      DisplayDTO displayDTO = mainService.getExchangeRates(currency);
      model.addAttribute("data", displayDTO);
      return "index";
   }
}
