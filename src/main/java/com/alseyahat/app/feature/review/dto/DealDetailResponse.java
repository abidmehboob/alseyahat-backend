package com.alseyahat.app.feature.review.dto;

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
	
	String name;

	String description;
	
	Double dealAverageRating;
	
	String city;
	
	String district;
	
}
