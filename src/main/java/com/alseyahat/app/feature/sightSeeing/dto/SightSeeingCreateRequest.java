package com.alseyahat.app.feature.sightSeeing.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SightSeeingCreateRequest {

	 @NotEmpty
	String name;

	String description;

//	String images;
	List<String> images = new ArrayList<>();

	String addressLine;

	String sightSeeingType;

	String city;
	
	String district;

	String town;
	
	 @NotNull
	double sightSeenFare;

	String postcode;

	Double latitude;

	Double longitude;
	
	boolean hotPrivateHired;
	
	boolean newArrival;
	
	boolean advertised;
}
