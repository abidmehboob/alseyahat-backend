package com.alseyahat.app.feature.hotel.dto;

import java.util.ArrayList;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class HotelFacilityCreateRequest {

	@NotEmpty
	ArrayList<HotelFacilityDto> hotelFacilitylst;
}
