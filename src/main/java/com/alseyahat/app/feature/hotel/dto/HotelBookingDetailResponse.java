package com.alseyahat.app.feature.hotel.dto;

import java.util.List;

import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HotelBookingDetailResponse {

	HotelDetailResponse hotel;
	
	CustomerBasicDetails customer;

	String bookingStatus;

	String routeID;

	String sartDate;
	
	String endDate;
	
	String paymentReceipt;
	
	String transactionId;
	
	String hotelBookingId;
	
	double totalRent;
	
	List<RoomBookingDetailResponse> rooms;
}
