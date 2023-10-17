package com.alseyahat.app.feature.deal.dto;

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
public class DealBookingUpdateRequest {

	@NotEmpty
	String dealBookingId;
	
	@NotEmpty
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED|CANCELED")
	String bookingStatus;
	
	Date bookedDate;
	
}
