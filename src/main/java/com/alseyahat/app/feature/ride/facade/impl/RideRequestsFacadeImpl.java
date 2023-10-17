package com.alseyahat.app.feature.ride.facade.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.ride.dto.RideCreateRequest;
import com.alseyahat.app.feature.ride.dto.RideCreateResponse;
import com.alseyahat.app.feature.ride.dto.RideDetailResponse;
import com.alseyahat.app.feature.ride.dto.RideUpdateRequest;
import com.alseyahat.app.feature.ride.dto.RideUpdateResponse;
import com.alseyahat.app.feature.ride.facade.RideRequestsFacade;
import com.alseyahat.app.feature.ride.repository.entity.QRideRequests;
import com.alseyahat.app.feature.ride.repository.entity.RideRequests;
import com.alseyahat.app.feature.ride.service.RideRequestsService;
import com.querydsl.core.types.Predicate;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RideRequestsFacadeImpl implements RideRequestsFacade {

	RideRequestsService rideRequestsService;
	ModelMapper modelMapper;
	
	@Override
	public void deleteRideRequest(String rideRequestsId) {
		RideRequests rideRequest = rideRequestsService.findOne(QRideRequests.rideRequests.rideRequestId.eq(rideRequestsId));
		if (rideRequest.isEnabled())
			rideRequest.setEnabled(Boolean.FALSE);
		else
			rideRequest.setEnabled(Boolean.TRUE);

		rideRequestsService.save(rideRequest);
		log.trace("RideRequests deleted with id [{}]", rideRequestsId);
	}

	@Override
	public RideDetailResponse findRideRequestId(String rideRequestId) {
		RideRequests rideRequest = rideRequestsService.findOne(QRideRequests.rideRequests.rideRequestId.eq(rideRequestId));
		return buildRideRequestDetailResponse(rideRequest);
	}

	@Override
	public RideCreateResponse createRideRequest(RideCreateRequest request) {
		final RideRequests savedRideRequest = rideRequestsService.save(buildRideRequest(request));
		return RideCreateResponse.builder().RideRequestId(savedRideRequest.getBookingId()).build();
	}

	@Override
	public RideUpdateResponse updateRideRequest(RideUpdateRequest request) {
		RideRequests rideRequest = rideRequestsService.findOne(QRideRequests.rideRequests.rideRequestId.eq(request.getRideRequestId()));
		final RideRequests savedRideRequest = rideRequestsService.save(buildRideRequest(rideRequest, request));
		return RideUpdateResponse.builder().RideRequestId(savedRideRequest.getRideRequestId()).build();
	}

	@Override
	public Page<RideDetailResponse> findAllRideRequest(Predicate predicate, Pageable pageable) {
		return rideRequestsService.findAll(predicate, pageable).map(this::buildRideRequestDetailResponse);
	}
	
	private RideDetailResponse buildRideRequestDetailResponse(final RideRequests rideRequest) {
		RideDetailResponse response = new RideDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RideRequests, RideDetailResponse> typeMap = modelMapper.typeMap(RideRequests.class, RideDetailResponse.class);
		typeMap.map(rideRequest, response);

		return response;
	}
	
	private RideRequests buildRideRequest(final RideCreateRequest request) {
		RideRequests rideRequest = new RideRequests();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<RideCreateRequest, RideRequests> typeMap = modelMapper.typeMap(RideCreateRequest.class, RideRequests.class);
		typeMap.addMappings(mapper -> mapper.skip(RideRequests::setRideRequestId));
		typeMap.addMappings(mapper -> mapper.skip(RideRequests::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RideRequests::setLastUpdated));
		typeMap.map(request, rideRequest);
//        category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return rideRequest;
	}
	
	private RideRequests buildRideRequest(RideRequests rideRequest, final RideUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RideUpdateRequest, RideRequests> typeMap = modelMapper.typeMap(RideUpdateRequest.class, RideRequests.class);
		typeMap.addMappings(mapper -> mapper.skip(RideRequests::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RideRequests::setLastUpdated));
		typeMap.map(request, rideRequest);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return rideRequest;
	}


}
