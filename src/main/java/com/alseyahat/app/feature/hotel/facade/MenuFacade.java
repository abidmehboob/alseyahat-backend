package com.alseyahat.app.feature.hotel.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.hotel.dto.MenuCreateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuCreateResponse;
import com.alseyahat.app.feature.hotel.dto.MenuDetailResponse;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface MenuFacade {
	
	void deleteMenu(final String menuId);

	MenuDetailResponse findMenuById(final String menuId);

	MenuCreateResponse createMenu(final MenuCreateRequest request);

	MenuUpdateResponse updateMenu(final MenuUpdateRequest request);

	Page<MenuDetailResponse> findAllMenu(final Predicate predicate, final Pageable pageable);
}

