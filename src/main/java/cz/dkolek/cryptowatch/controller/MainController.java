package cz.dkolek.cryptowatch.controller;

import cz.dkolek.cryptowatch.service.MainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@Controller
public class MainController {

   private final MainService mainService;

   @Autowired
   public MainController(MainService mainService) {
      this.mainService = mainService;
   }

   @GetMapping("/index")
   public String index(Model model) {
//      List<List<Object>> data = mainService.callApi();
      // TODO: Remove when ready - using mock data to avoid getting locked out of calling API again...
      List<List<Object>> data = mainService.getMockData();
      model.addAttribute("data", data);
      return "index";
   }
}
