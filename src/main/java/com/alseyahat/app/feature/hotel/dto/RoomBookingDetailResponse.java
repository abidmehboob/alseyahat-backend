package com.alseyahat.app.feature.hotel.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoomBookingDetailResponse {

	String roomBookingId;
		
	String roomType;

	Integer roomCount;
		
	Integer childern;
	
	Integer adult;
	
	Integer extraMatress;
}
