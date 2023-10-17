package com.alseyahat.app.feature.vehicle.hired.dto;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PrivateHiredCreateRequest {

	@NotEmpty
	String name;
	String description;
	String type;
	@NotEmpty
	String numberOfSeat;
	
	boolean driverRequired;
	
	boolean fuel;
	
	double perDayRate;
	Double latitude;
	Double longitude;
	String district;
	List<String> images = new ArrayList<>();
	
	String city;
	
	String area;
}
