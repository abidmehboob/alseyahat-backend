package com.alseyahat.app.feature.vehicle.hired.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PrivateHiredBookingUpdateRequest {

	@NotEmpty
	String privateHiredBookingId;
	
	@NotEmpty
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED|CANCELED")
	String bookingStatus;
	
	Date startDate;
	
	Date endDate;
	
	String pickLocation;
	
	String paymentReceipt;
	
	String transactionId;
}
