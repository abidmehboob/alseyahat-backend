package com.alseyahat.app.feature.driver.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.alseyahat.app.feature.driver.repository.entity.DriverBooking;

public interface DriverBookingRepository extends PagingAndSortingRepository<DriverBooking, Long>, QuerydslPredicateExecutor<DriverBooking> {

}
