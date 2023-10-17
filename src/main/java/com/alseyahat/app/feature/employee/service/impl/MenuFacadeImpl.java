package com.alseyahat.app.feature.employee.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.feature.hotel.dto.MenuCreateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuCreateResponse;
import com.alseyahat.app.feature.hotel.dto.MenuDetailResponse;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateResponse;
import com.alseyahat.app.feature.hotel.facade.MenuFacade;
import com.querydsl.core.types.Predicate;

public class MenuFacadeImpl implements MenuFacade {

	@Override
	public void deleteMenu(String menuId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MenuDetailResponse findMenuById(String menuId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuCreateResponse createMenu(MenuCreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MenuUpdateResponse updateMenu(MenuUpdateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<MenuDetailResponse> findAllMenu(Predicate predicate, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

}
