package com.alseyahat.app.feature.sightSeeing.dto;

import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SightSeeingBookingDetailResponse {

	String sightSeeingBookingId;
	
    SightSeeingDetailResponse sightSeeing;
    
    CustomerBasicDetails customer;
	
    String bookedDate;
	
	String pickLocation;
	
	 String bookingStatus;
	
	String paymentReceipt;
	
	String transactionId;
	
	 double totalFare;
}
