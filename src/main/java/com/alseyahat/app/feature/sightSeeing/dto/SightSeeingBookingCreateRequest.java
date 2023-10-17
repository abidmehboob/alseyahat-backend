package com.alseyahat.app.feature.sightSeeing.dto;

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
public class SightSeeingBookingCreateRequest {

	@NotEmpty
	String sightSeeingId;
	
	@DateTimeFormat(iso=ISO.DATE)
	Date bookedDate;
	
	
	Double pickupLatitude;
	
	
	Double pickupLongitude;
	
	@NotEmpty
	String pickUpTime;
	
	@NotEmpty
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED")
	String bookingStatus;
	
	String pickLocation;

}

