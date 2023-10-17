package com.alseyahat.app.feature.hotel.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoomTypeDetailResponse {

	String roomTypeId;
	String type;
	String hotelId;
	String charges;
	String name;
	String description;
	String email;
	String phone;
	Double hotelAverageRating;
	int reviewsCount;
	String city;
}
