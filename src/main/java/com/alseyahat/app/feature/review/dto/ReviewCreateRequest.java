package com.alseyahat.app.feature.review.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import java.util.Date;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReviewCreateRequest {

	String hotelId;

	String sightSeeingId;

	String dealId;
	
	String privateHiredId;

	String reviewFor;

	Integer rating;

	String review;

	boolean isEnabled = Boolean.TRUE;

	Date dateCreated;

	Date lastUpdated;
}
