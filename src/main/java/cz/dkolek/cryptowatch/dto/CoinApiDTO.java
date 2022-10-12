package cz.dkolek.cryptowatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CoinApiDTO {
   private ZonedDateTime time;
   private String assetIdBase;
   private String assetIdQuote;
   private BigDecimal rate;
}
