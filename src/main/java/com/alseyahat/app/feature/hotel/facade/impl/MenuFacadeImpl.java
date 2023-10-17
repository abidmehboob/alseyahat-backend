package com.alseyahat.app.feature.hotel.facade.impl;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.feature.hotel.dto.MenuCreateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuCreateResponse;
import com.alseyahat.app.feature.hotel.dto.MenuDetailResponse;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateResponse;
import com.alseyahat.app.feature.hotel.facade.MenuFacade;
import com.alseyahat.app.feature.hotel.repository.entity.QRestaurantMenu;
import com.alseyahat.app.feature.hotel.repository.entity.RestaurantMenu;
import com.alseyahat.app.feature.hotel.service.RestaurantMenuService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MenuFacadeImpl implements MenuFacade {
	
	RestaurantMenuService restaurantMenuService;
	
    ModelMapper modelMapper;
    
	@Override
	public void deleteMenu(String menuId) {
		RestaurantMenu menu = restaurantMenuService.findOne(QRestaurantMenu.restaurantMenu.menuId.eq(menuId));
		if (menu.isEnabled())
			menu.setEnabled(Boolean.FALSE);
		else
			menu.setEnabled(Boolean.TRUE);

		restaurantMenuService.save(menu);
		log.trace("Menu deleted with id [{}]", menuId);
	}

	@Override
	public MenuDetailResponse findMenuById(String menuId) {
		RestaurantMenu restaurantMenu = restaurantMenuService.findOne(QRestaurantMenu.restaurantMenu.menuId.eq(menuId));
		return buildMenuDetailResponse(restaurantMenu);
	}

	@Override
	public MenuCreateResponse createMenu(MenuCreateRequest request) {
		final RestaurantMenu savedrestaurantMenu = restaurantMenuService.save(buildMenu(request));
		return MenuCreateResponse.builder().menuId(savedrestaurantMenu.getMenuId()).build();
	}

	@Override
	public MenuUpdateResponse updateMenu(MenuUpdateRequest request) {
		RestaurantMenu restaurantMenu = restaurantMenuService.findOne(QRestaurantMenu.restaurantMenu.menuId.eq(request.getMenuId()));
		final RestaurantMenu savedrestaurantMenu = restaurantMenuService.save(buildMenu(restaurantMenu, request));
		return MenuUpdateResponse.builder().menuId(savedrestaurantMenu.getMenuId()).build();
	}

	@Override
	public Page<MenuDetailResponse> findAllMenu(Predicate predicate, Pageable pageable) {
		return restaurantMenuService.findAll(predicate, pageable).map(this::buildMenuDetailResponse);
	}
	
	private MenuDetailResponse buildMenuDetailResponse(final RestaurantMenu restaurantMenu) {
		MenuDetailResponse response = new MenuDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RestaurantMenu, MenuDetailResponse> typeMap = modelMapper.typeMap(RestaurantMenu.class, MenuDetailResponse.class);
		typeMap.map(restaurantMenu, response);
		response.setHotelId(restaurantMenu.getHotel().getHotelId());
	
		return response;
	}
	
	private RestaurantMenu buildMenu(final MenuCreateRequest request) {
		RestaurantMenu restaurantMenu = new RestaurantMenu();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<MenuCreateRequest, RestaurantMenu> typeMap = modelMapper.typeMap(MenuCreateRequest.class, RestaurantMenu.class);
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setMenuId));
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setLastUpdated));
		typeMap.map(request, restaurantMenu);
		
		return restaurantMenu;
	}
	
	private RestaurantMenu buildMenu(RestaurantMenu restaurantMenu, final MenuUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<MenuUpdateRequest, RestaurantMenu> typeMap = modelMapper.typeMap(MenuUpdateRequest.class, RestaurantMenu.class);
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setLastUpdated));
		typeMap.map(request, restaurantMenu);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return restaurantMenu;
	}

}
