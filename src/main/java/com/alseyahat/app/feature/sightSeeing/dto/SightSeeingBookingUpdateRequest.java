package com.alseyahat.app.feature.sightSeeing.dto;
import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class SightSeeingBookingUpdateRequest {
	
	@NotEmpty
    String sightSeeingBookingId;
    
	
	Date bookedDate;
	
	Double pickupLatitude;
	
	Double pickupLongitude;
	
	String pickUpTime;
	
	@NotEmpty
	@Pattern(regexp = "CANCELED|CREATED|ACCEPTED|REJECTED|COMPLETED")
	String bookingStatus;
	
	String pickLocation;
		
}
