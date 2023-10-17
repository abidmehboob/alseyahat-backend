package com.alseyahat.app.feature.review.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ReviewCreateResponse {
	String ReviewRatingId;
	Double averageRating;

	Integer totalReviews;

	Integer rating1;

	Integer rating2;

	Integer rating3;

	Integer rating4;

	Integer rating5;

}
