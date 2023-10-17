package com.alseyahat.app.feature.review.dto;

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

	String city;

	String district;

	Double sightAverageRating;
}
