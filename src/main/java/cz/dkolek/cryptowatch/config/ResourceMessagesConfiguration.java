package cz.dkolek.cryptowatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class ResourceMessagesConfiguration {

   @Bean
   public ResourceBundleMessageSource messageSource() {
      ResourceBundleMessageSource source = new ResourceBundleMessageSource();
      source.setBasenames("messages");
      return source;
   }
}
