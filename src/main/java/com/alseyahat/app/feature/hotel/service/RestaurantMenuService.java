package com.alseyahat.app.feature.hotel.service;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.hotel.repository.entity.RestaurantMenu;
import com.querydsl.core.types.Predicate;

public interface RestaurantMenuService {
	RestaurantMenu save(final RestaurantMenu entity);

	RestaurantMenu findOne(final Predicate predicate);

    Optional<RestaurantMenu> find_One(final Predicate predicate);

    Page<RestaurantMenu> findAll(final Predicate predicate, final Pageable pageable);
    
    List<RestaurantMenu> saveAll(List<RestaurantMenu> restaurantMenuLst);

}