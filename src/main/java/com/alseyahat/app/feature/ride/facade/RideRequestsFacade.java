package com.alseyahat.app.feature.ride.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.ride.dto.RideCreateRequest;
import com.alseyahat.app.feature.ride.dto.RideCreateResponse;
import com.alseyahat.app.feature.ride.dto.RideDetailResponse;
import com.alseyahat.app.feature.ride.dto.RideUpdateRequest;
import com.alseyahat.app.feature.ride.dto.RideUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface RideRequestsFacade {
	
	void deleteRideRequest(final String rideRequestId);

	RideDetailResponse findRideRequestId(final String rideRequestId);

	RideCreateResponse createRideRequest(final RideCreateRequest request);

	RideUpdateResponse updateRideRequest(final RideUpdateRequest request);

	Page<RideDetailResponse> findAllRideRequest(final Predicate predicate, final Pageable pageable);
}