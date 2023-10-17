package com.alseyahat.app.feature.hotel.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HotelFacilityDto {

	String hotelFacilityId;

	String name;

	String description;
}
