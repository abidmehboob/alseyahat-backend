package com.alseyahat.app.feature.ride.dto;

import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class BookingDetailResponse {
	
	String bookingId;

	CustomerBasicDetails customer;

	String dropOffAddress;

	double dropOffLat;

	double dropOffLong;

	String dropOffTime;

	String pickUpAddress;

	double pickUpLat;

	double pickUpLong;

	String pickUpTime;

	String rideStatus;

	String routeID;

	String selectedDay;
	
	double totalTraval;
	    
	double totalRideFare;
	
	int fareRate;
}
