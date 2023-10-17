package com.alseyahat.app.feature.hotel.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.alseyahat.app.feature.hotel.repository.entity.RoomBooking;

public interface RoomBookingRepository extends PagingAndSortingRepository<RoomBooking, Long>, QuerydslPredicateExecutor<RoomBooking> {

}