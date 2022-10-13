package cz.dkolek.cryptowatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DisplayDTO {
   private String timestamp;
   private Double rate;
}
