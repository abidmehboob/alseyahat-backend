package com.alseyahat.app.feature.review.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReviewDetailResponse {

	String reviewRatingId;

	HotelDetailResponse hotel;

	SightSeeingDetailResponse sightSeeing;

	DealDetailResponse deal;
	
	PrivateHiredDetailResponse privateHired;

	String reviewFor;

	Integer rating;

	String review;
	
	String customerId;

}
