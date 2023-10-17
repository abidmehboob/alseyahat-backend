package com.alseyahat.app.feature.wallet.dto;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransactionDetailResponse {

	Integer id;

	String TransactionType;

	BigDecimal amount;

	String phone;

	String customerName;

	String userId;

	String description;

	String lastUpdatedBy;

	Date dateCreated;

	Date lastUpdated;
}
