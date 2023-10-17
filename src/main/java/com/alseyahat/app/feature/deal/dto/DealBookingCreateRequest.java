package com.alseyahat.app.feature.deal.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DealBookingCreateRequest {

	@NotEmpty(message = "Deal Id is required")
	String dealId;
	
	@NotEmpty(message = "Booking Status is required")
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED")
	String bookingStatus;
	
	@DateTimeFormat(iso=ISO.DATE)
	Date bookedDate;
	
}