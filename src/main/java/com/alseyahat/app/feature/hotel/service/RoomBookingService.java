package com.alseyahat.app.feature.hotel.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.hotel.repository.entity.RoomBooking;
import com.querydsl.core.types.Predicate;

public interface RoomBookingService {
	RoomBooking save(final RoomBooking entity);

	RoomBooking findOne(final Predicate predicate);

    Optional<RoomBooking> find_One(final Predicate predicate);

    Page<RoomBooking> findAll(final Predicate predicate, final Pageable pageable);
    
    List<RoomBooking> saveAll(List<RoomBooking> roomTypeLst);

}