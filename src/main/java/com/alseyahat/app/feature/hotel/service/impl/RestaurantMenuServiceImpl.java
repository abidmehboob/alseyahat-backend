package com.alseyahat.app.feature.hotel.service.impl;

import java.util.List;
import java.util.Optional;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.hotel.repository.RestaurantMenuRepository;
import com.alseyahat.app.feature.hotel.repository.entity.RestaurantMenu;
import com.alseyahat.app.feature.hotel.service.RestaurantMenuService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;


@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RestaurantMenuServiceImpl implements RestaurantMenuService{
	
	RestaurantMenuRepository restaurantMenuRepository;

	@Override
	public RestaurantMenu save(RestaurantMenu entity) {
		return restaurantMenuRepository.save(entity);
	}

	@Override
	public RestaurantMenu findOne(Predicate predicate) {
		return restaurantMenuRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.restaurant_entity_not_found"));
	}

	@Override
	public Optional<RestaurantMenu> find_One(Predicate predicate) {
		return restaurantMenuRepository.findOne(predicate);
	}

	@Override
	public Page<RestaurantMenu> findAll(Predicate predicate, Pageable pageable) {
		return restaurantMenuRepository.findAll(predicate, pageable);
	}
	
	@Override
	public List<RestaurantMenu> saveAll(List<RestaurantMenu> restaurantMenuLst) {
		return IterableUtils.toList(restaurantMenuRepository.saveAll(restaurantMenuLst));
	}

}
