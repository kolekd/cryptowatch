package cz.dkolek.cryptowatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ERHostResponseDTO {
   private Map<String, Map<String, Double>> rates;
}
