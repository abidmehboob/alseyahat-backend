package com.alseyahat.app.feature.vehicle.hired.facade;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingCreateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingCreateResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingUpdateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface PrivateHiredBookingFacade {

	void deletePrivateHiredBooking(final String privateHiredBookingId);

	PrivateHiredBookingDetailResponse findPrivateHiredBookingId(final String privateHiredBookingId);

	PrivateHiredBookingCreateResponse createPrivateHiredBooking(final PrivateHiredBookingCreateRequest request);

	PrivateHiredBookingUpdateResponse updatePrivateHiredBooking(final PrivateHiredBookingUpdateRequest request);

	Page<PrivateHiredBookingDetailResponse> findAllPrivateHiredBooking(final Predicate predicate, final Pageable pageable);
	
	Page<PrivateHiredBookingDetailResponse> findAllPrivateHiredBooking(final Map<String, Object> parameters, final Pageable pageable);
}