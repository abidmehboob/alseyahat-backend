package com.alseyahat.app.feature.sightSeeing.dto;

import java.util.List;

import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SightSeeingDetailResponse {

	String sightSeeingId;
	
	String name;

	String description;

	String logo;

	String images;

	String addressLine;

	String sightSeeingType;

	String city;
	
	String district;

	String town;

	String postcode;

	Double latitude;

	Double longitude;
	
	Double sightAverageRating;
	
	List<ReviewDto> reviewLst;
	
	double sightSeenFare;
	
	double totalFare;
	
	boolean hotSight;
	
	boolean newArrival;
	
	boolean advertised;
}
