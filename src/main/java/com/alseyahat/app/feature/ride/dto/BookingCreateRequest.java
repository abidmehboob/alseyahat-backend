package com.alseyahat.app.feature.ride.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookingCreateRequest {

	@NotEmpty
	String dropOffAddress;
	
	double dropOffLat;
	
	double dropOffLong;
	
	@DateTimeFormat(iso=ISO.DATE)
	Date dropOffTime;
	
	@NotEmpty
	String pickUpAddress;
	
	double pickUpLat;
	
	double pickUpLong;
	
	@DateTimeFormat(iso=ISO.TIME)
	Date pickUpTime;
	
	@NotEmpty
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED|CANCELED")
	String rideStatus;

	String routeID;
	
//	@DateTimeFormat(iso=ISO.DATE)
//	Date selectedDay;
	
	String totalTraval;

}