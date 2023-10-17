package com.alseyahat.app.feature.wallet.dto;

import java.math.BigDecimal;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class WalletUpdateRequest {
	
	private BigDecimal balance;
	
	String paymentReceipt;

}
