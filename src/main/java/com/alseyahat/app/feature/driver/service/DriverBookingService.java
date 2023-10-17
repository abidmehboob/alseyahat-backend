package com.alseyahat.app.feature.driver.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.driver.repository.entity.DriverBooking;
import com.querydsl.core.types.Predicate;

public interface DriverBookingService {
	
	DriverBooking save(final DriverBooking entity);

	DriverBooking findOne(final Predicate predicate);

    Optional<DriverBooking> find_One(final Predicate predicate);

    Page<DriverBooking> findAll(final Predicate predicate, final Pageable pageable);


}