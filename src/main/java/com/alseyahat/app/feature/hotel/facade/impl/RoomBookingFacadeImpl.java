package com.alseyahat.app.feature.hotel.facade.impl;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.feature.hotel.dto.RoomBookingCreateRequest;
import com.alseyahat.app.feature.hotel.dto.RoomBookingCreateResponse;
import com.alseyahat.app.feature.hotel.dto.RoomBookingDetailResponse;
import com.alseyahat.app.feature.hotel.dto.RoomBookingUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.RoomBookingUpdateResponse;
import com.alseyahat.app.feature.hotel.facade.RoomBookingFacade;
import com.alseyahat.app.feature.hotel.repository.entity.QRoomBooking;
import com.alseyahat.app.feature.hotel.repository.entity.RoomBooking;
import com.alseyahat.app.feature.hotel.service.RoomBookingService;
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
public class RoomBookingFacadeImpl implements RoomBookingFacade {
    
	RoomBookingService roomBookingService;
	
    ModelMapper modelMapper;
    
	@Override
	public void deleteRoomBooking(String roomBookingId) {
		RoomBooking roomBooking = roomBookingService.findOne(QRoomBooking.roomBooking.roomBookingId.eq(roomBookingId));
		if (roomBooking.isEnabled())
			roomBooking.setEnabled(Boolean.FALSE);
		else
			roomBooking.setEnabled(Boolean.TRUE);

		roomBookingService.save(roomBooking);
		log.trace("Review deleted with id [{}]", roomBookingId);
	}

	@Override
	public RoomBookingDetailResponse findRoomBookingId(String roomBookingId) {
		RoomBooking roomBooking = roomBookingService.findOne(QRoomBooking.roomBooking.roomBookingId.eq(roomBookingId));
		return buildRoomBookingDetailResponse(roomBooking);
	}

	@Override
	public RoomBookingCreateResponse createRoomBooking(RoomBookingCreateRequest request) {
		final RoomBooking savedRoomBooking = roomBookingService.save(buildRoomBooking(request));
		return RoomBookingCreateResponse.builder().roomBookingId(savedRoomBooking.getRoomBookingId()).build();
	}

	@Override
	public RoomBookingUpdateResponse updateRoomBooking(RoomBookingUpdateRequest request) {
		RoomBooking roomBooking = roomBookingService.findOne(QRoomBooking.roomBooking.roomBookingId.eq(request.getRoomBookingId()));
		final RoomBooking savedRoomBooking = roomBookingService.save(buildRoomBooking(roomBooking, request));
		return RoomBookingUpdateResponse.builder().roomBookingId(savedRoomBooking.getRoomBookingId()).build();
	}

	@Override
	public Page<RoomBookingDetailResponse> findAllRoomBookings(final Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QRoomBooking.roomBooking);
		log.trace("Finding AllRoomBookings predicate [{}]", predicate);
		return roomBookingService.findAll(predicate, pageable).map(this::buildRoomBookingDetailResponse);
	}
	
	private RoomBookingDetailResponse buildRoomBookingDetailResponse(final RoomBooking roomBooking) {
		RoomBookingDetailResponse response = new RoomBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RoomBooking, RoomBookingDetailResponse> typeMap = modelMapper.typeMap(RoomBooking.class, RoomBookingDetailResponse.class);
		typeMap.map(roomBooking, response);
//		response.setHotelId(roomBooking.getHotel().getHotelId());
//		response.setEmail(roomBooking.getHotel().getEmail());
//		response.setName(roomBooking.getHotel().getName());
//		response.setPhone(roomBooking.getHotel().getPhone());
//		response.setHotelAverageRating(roomBooking.getHotel().getItemAverageRating());
//		response.setDescription(roomBooking.getHotel().getDescription());
//		response.setCity(roomBooking.getHotel().getCity());
//		response.setReviewsCount(roomBooking.getHotel().getReview().size());
		return response;
	}
	
	private RoomBooking buildRoomBooking(final RoomBookingCreateRequest request) {
		RoomBooking roomBooking = new RoomBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<RoomBookingCreateRequest, RoomBooking> typeMap = modelMapper.typeMap(RoomBookingCreateRequest.class, RoomBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setRoomBookingId));
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setLastUpdated));
		typeMap.map(request, roomBooking);
		
		return roomBooking;
	}
	
	private RoomBooking buildRoomBooking(RoomBooking roomBooking, final RoomBookingUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RoomBookingUpdateRequest, RoomBooking> typeMap = modelMapper.typeMap(RoomBookingUpdateRequest.class, RoomBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setLastUpdated));
		typeMap.map(request, roomBooking);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return roomBooking;
	}

}
