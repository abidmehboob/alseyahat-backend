package com.alseyahat.app.feature.hotel.facade;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import com.alseyahat.app.feature.hotel.dto.HotelBookingCreateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelBookingCreateResponse;
import com.alseyahat.app.feature.hotel.dto.HotelBookingDetailResponse;
import com.alseyahat.app.feature.hotel.dto.HotelBookingUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelBookingUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface HotelBookingFacade {

	void deleteHotelBooking(final String hotelBookingId);

	HotelBookingDetailResponse findHotelBookingId(final String hotelBookingId);

	HotelBookingCreateResponse createHotelBooking(final HotelBookingCreateRequest request);

	HotelBookingUpdateResponse updateHotelBooking(final HotelBookingUpdateRequest request);

	Page<HotelBookingDetailResponse> findAllHotelBooking(final Predicate predicate, final Pageable pageable);
	Page<HotelBookingDetailResponse> findAllHotelBooking(@RequestParam final Map<String, Object> parameters, final Pageable pageable);
	 
}