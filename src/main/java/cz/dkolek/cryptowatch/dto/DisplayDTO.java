package cz.dkolek.cryptowatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DisplayDTO {
   private String timestamp;
   private String currency;
   private Double rate;
}
