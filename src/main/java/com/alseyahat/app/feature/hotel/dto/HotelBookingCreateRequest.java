package com.alseyahat.app.feature.hotel.dto;

import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotEmpty;
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
public class HotelBookingCreateRequest {

	@NotEmpty
	String hotelId;
	@NotEmpty
	String bookingStatus;
	String roomType;
	Integer roomCount;
	Integer childern;
	Integer adult;
	Integer extraMatress;
	String pickLocation;
	@DateTimeFormat(iso=ISO.DATE)
	Date startDate;
	@DateTimeFormat(iso=ISO.DATE)
	Date endDate;
	List<RoomBookingCreateRequest> rooms;
	
}
