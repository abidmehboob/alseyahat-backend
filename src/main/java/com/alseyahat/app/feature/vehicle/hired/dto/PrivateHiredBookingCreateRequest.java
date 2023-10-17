package com.alseyahat.app.feature.vehicle.hired.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PrivateHiredBookingCreateRequest {

	@NotEmpty
	String privateHiredId;

	@NotEmpty
	@Pattern(regexp = "CREATED|ACCEPTED|REJECTED|COMPLETED")
	String bookingStatus;
	
	@DateTimeFormat(iso=ISO.DATE)
	Date startDate;
	
	@DateTimeFormat(iso=ISO.DATE)
	Date endDate;

	String city;

	String district;

	Double latitude;

	Double longitude;

	String pickLocation;
	
	String addtionalRequest;
}
