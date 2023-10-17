package com.alseyahat.app.feature.hotel.dto;

import java.util.Date;

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
public class HotelBookingUpdateRequest {

	String hotelBookingId;

	String bookingStatus;

	String roomType;
	
	Integer roomCount;

	Integer childern;

	Integer adult;

	Integer extraMatress;

	String pickLocation;

	@DateTimeFormat(iso = ISO.DATE)
	Date startDate;
	@DateTimeFormat(iso = ISO.DATE)
	Date endDate;

	String paymentReceipt;

	String transactionId;
}
