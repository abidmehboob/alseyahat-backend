package com.alseyahat.app.feature.hotel.service.impl;

import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.hotel.repository.RoomBookingRepository;
import com.alseyahat.app.feature.hotel.repository.entity.RoomBooking;
import com.alseyahat.app.feature.hotel.service.RoomBookingService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RoomBookingServiceImpl implements RoomBookingService{
	
	RoomBookingRepository roomBookingRepository;

	@Override
	public RoomBooking save(RoomBooking entity) {
		return roomBookingRepository.save(entity);
	}

	@Override
	public RoomBooking findOne(Predicate predicate) {
		return roomBookingRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.booking_entity_not_found"));
	}

	@Override
	public Optional<RoomBooking> find_One(Predicate predicate) {
		return roomBookingRepository.findOne(predicate);
	}

	@Override
	public Page<RoomBooking> findAll(Predicate predicate, Pageable pageable) {
		return roomBookingRepository.findAll(predicate, pageable);
	}
	
	@Override
	public List<RoomBooking> saveAll(List<RoomBooking> roomTypeLst) {
		return IterableUtils.toList(roomBookingRepository.saveAll(roomTypeLst));
	}

}
