package com.alseyahat.app.feature.hotel.controller;

import java.awt.Menu;
import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alseyahat.app.feature.hotel.dto.MenuCreateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuCreateResponse;
import com.alseyahat.app.feature.hotel.dto.MenuDetailResponse;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.MenuUpdateResponse;
import com.alseyahat.app.feature.hotel.facade.MenuFacade;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@Api(tags = "Menu")
@RequestMapping("/menus")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MenuController {

	MenuFacade menuFacade;
		
	  @PostMapping
	    @ApiOperation(value = "Create menu", nickname = "createMenu", notes = "Create menu")
	    public ResponseEntity<MenuCreateResponse> createMenu(@Valid @RequestBody final MenuCreateRequest menuRequest) {
	        return new ResponseEntity<>(menuFacade.createMenu(menuRequest), HttpStatus.CREATED);
	    }

	    @PatchMapping
	    @ApiOperation(value = "Update Menu", nickname = "updateMenu", notes = "Update menu")
	    public ResponseEntity<MenuUpdateResponse> updateMenu(@Valid @RequestBody final MenuUpdateRequest menuUpdateRequest) {
	        return ResponseEntity.ok(menuFacade.updateMenu(menuUpdateRequest));
	    }

	    @GetMapping
	    @ApiOperation(value = "Get all Menus", nickname = "getAllMenus", notes = "Get all Menus")
	    public ResponseEntity<Page<MenuDetailResponse>> getAllMenus(@QuerydslPredicate(root = Menu.class) final Predicate predicate, final Pageable pageable) {
	         return ResponseEntity.ok(menuFacade.findAllMenu(predicate, pageable));
	    }

	    @GetMapping("/{menuId}")
	    @ApiOperation(value = "Get RoomType by id", nickname = "getMenuById", notes = "Get Menu by id")
	    public ResponseEntity<MenuDetailResponse> getMenuById(@PathVariable final String menuId) {
	        return ResponseEntity.ok(menuFacade.findMenuById(menuId));
	    }

	    @DeleteMapping("/{menuId}")
	    @ApiOperation(value = "Delete Menu", nickname = "deleteMenu", notes = "Delete Menu")
	    public ResponseEntity<Void> deleteMenu(@PathVariable final String menuId) {
	    	menuFacade.deleteMenu(menuId);
	        return ResponseEntity.ok().build();
	    }

}
