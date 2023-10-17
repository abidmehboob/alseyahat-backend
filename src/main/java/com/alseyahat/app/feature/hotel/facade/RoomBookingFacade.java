package com.alseyahat.app.feature.hotel.facade;

import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.hotel.dto.RoomBookingCreateRequest;
import com.alseyahat.app.feature.hotel.dto.RoomBookingCreateResponse;
import com.alseyahat.app.feature.hotel.dto.RoomBookingDetailResponse;
import com.alseyahat.app.feature.hotel.dto.RoomBookingUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.RoomBookingUpdateResponse;

public interface RoomBookingFacade {

	void deleteRoomBooking(final String roomBookingId);

	RoomBookingDetailResponse findRoomBookingId(final String roomBookingId);

	RoomBookingCreateResponse createRoomBooking(final RoomBookingCreateRequest request);

	RoomBookingUpdateResponse updateRoomBooking(final RoomBookingUpdateRequest request);

	Page<RoomBookingDetailResponse> findAllRoomBookings(final Map<String, Object> parameters, final Pageable pageable);
}