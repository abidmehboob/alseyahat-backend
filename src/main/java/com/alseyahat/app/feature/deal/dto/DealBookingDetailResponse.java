package com.alseyahat.app.feature.deal.dto;

import java.util.Date;

import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DealBookingDetailResponse {
	
	String dealBookingId;
	DealDetailResponse dealDetailResponse;
	CustomerBasicDetails customer;
	String bookingStatus;
	String paymentReceipt;
	Date bookedDate;

}
