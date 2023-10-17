package com.alseyahat.app.feature.driver.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.driver.repository.DriverBookingRepository;
import com.alseyahat.app.feature.driver.repository.entity.DriverBooking;
import com.alseyahat.app.feature.driver.service.DriverBookingService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DriverBookingServiceImpl implements DriverBookingService {

	DriverBookingRepository driverBookingRepository;
	
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public DriverBooking save(DriverBooking entity) {
		return driverBookingRepository.save(entity);
	}

	@Override
	public DriverBooking findOne(Predicate predicate) {
		return driverBookingRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.store_entity_not_found"));
	}

	@Override
	public Optional<DriverBooking> find_One(Predicate predicate) {
		return driverBookingRepository.findOne(predicate);
	}

	@Override
	public Page<DriverBooking> findAll(Predicate predicate, Pageable pageable) {
		return driverBookingRepository.findAll(predicate, pageable);
	}
	
}
