package com.alseyahat.app.feature.deal.dto;

import java.util.List;

import com.alseyahat.app.feature.hotel.dto.HotelDetailResponse;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DealDetailResponse {

	String dealId;
	
	HotelDetailResponse hotelDetailResponse;
	
	SightSeeingDetailResponse sightSeeingDetailResponse;
	
	PrivateHiredDetailResponse privateHiredDetailResponse;
	
	String name;

	String description;
	
	double dealAmount;
	
	Double dealAverageRating;
	
	Double latitude;
	
	Double longitude;
	
	String district;
	
	List<ReviewDto> reviewLst;
	
	String images;
	
	String city;
	
	String area;
	
	String dealType;
}
