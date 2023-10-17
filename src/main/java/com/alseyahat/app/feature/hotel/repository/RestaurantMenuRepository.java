package com.alseyahat.app.feature.hotel.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.alseyahat.app.feature.hotel.repository.entity.RestaurantMenu;

public interface RestaurantMenuRepository extends PagingAndSortingRepository<RestaurantMenu, Long>, QuerydslPredicateExecutor<RestaurantMenu> {

}