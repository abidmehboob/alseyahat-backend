package com.alseyahat.app.feature.hotel.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.feature.hotel.dto.HotelCreateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelCreateResponse;
import com.alseyahat.app.feature.hotel.dto.HotelDetailResponse;
import com.alseyahat.app.feature.hotel.dto.HotelFacilityDto;
import com.alseyahat.app.feature.hotel.dto.HotelUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelUpdateResponse;
import com.alseyahat.app.feature.hotel.dto.RestaurantMenuDto;
import com.alseyahat.app.feature.hotel.dto.RoomTypeDto;
import com.alseyahat.app.feature.hotel.facade.HotelFacade;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.hotel.repository.entity.HotelFacility;
import com.alseyahat.app.feature.hotel.repository.entity.QHotel;
import com.alseyahat.app.feature.hotel.repository.entity.QHotelFacility;
import com.alseyahat.app.feature.hotel.repository.entity.QRestaurantMenu;
import com.alseyahat.app.feature.hotel.repository.entity.QRoomType;
import com.alseyahat.app.feature.hotel.repository.entity.RestaurantMenu;
import com.alseyahat.app.feature.hotel.repository.entity.RoomType;
import com.alseyahat.app.feature.hotel.service.HotelFacilityService;
import com.alseyahat.app.feature.hotel.service.HotelService;
import com.alseyahat.app.feature.hotel.service.RestaurantMenuService;
import com.alseyahat.app.feature.hotel.service.RoomTypeService;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import static com.alseyahat.app.commons.Constants.SEPARATOR;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class HotelFacadeImpl implements HotelFacade {

	HotelService hotelService;

	ModelMapper modelMapper;

	HotelFacilityService hotelFacilityService;

	ReviewService reviewService;

	RoomTypeService roomTypeService;

	RestaurantMenuService restaurantMenuService;

	@Override
	public void deleteHotel(String hotelId) {
		Hotel hotel = hotelService.findOne(QHotel.hotel.hotelId.eq(hotelId));
		if (hotel.isEnabled())
			hotel.setEnabled(Boolean.FALSE);
		else
			hotel.setEnabled(Boolean.TRUE);

		hotelService.save(hotel);
		log.trace("Review deleted with id [{}]", hotelId);
	}

	@Override
	public HotelDetailResponse findHotelId(String hotelId) {
		Hotel hotel = hotelService.findOne(QHotel.hotel.hotelId.eq(hotelId).and(QHotel.hotel.isEnabled.isTrue()));
		return buildHotelDetailResponse(hotel);
	}

	@Override
	public HotelCreateResponse createHotel(HotelCreateRequest request) {
		final Hotel savedHotel = hotelService.save(buildHotel(request));
		if (request.getRoomTypes() != null) {
			List<RoomType> roomTypes = request.getRoomTypes().stream()
					.map(roomTyp -> buildRoomType(roomTyp, savedHotel)).collect(Collectors.toList());
			if (!roomTypes.isEmpty())
				roomTypeService.saveAll(roomTypes);
		}

		if (request.getFoodMenu() != null) {
			List<RestaurantMenu> restaurantMenus = request.getFoodMenu().stream()
					.map(foodmenu -> buildRestaurantMenu(foodmenu, savedHotel)).collect(Collectors.toList());
			if (!restaurantMenus.isEmpty())
				restaurantMenuService.saveAll(restaurantMenus);
		}
		if (request.getHotelFacilites() != null) {
			List<HotelFacility> hotelFacilities = request.getHotelFacilites().stream()
					.map(facility -> buildHotelFacility(facility, savedHotel)).collect(Collectors.toList());
			if (!hotelFacilities.isEmpty())
				hotelFacilityService.saveAll(hotelFacilities);
		}

		return HotelCreateResponse.builder().hotelId(savedHotel.getHotelId()).build();
	}

	@Override
	public HotelUpdateResponse updateHotel(HotelUpdateRequest request) {
		Hotel hotel = hotelService.findOne(QHotel.hotel.hotelId.eq(request.getHotelId()));
		final Hotel savedHotel = hotelService.save(buildHotel(hotel, request));
		return HotelUpdateResponse.builder().hotelId(savedHotel.getHotelId()).build();
	}

	@Override
	public Page<HotelDetailResponse> findAllHotel(Predicate predicate, Pageable pageable) {
		return hotelService.findAll(ExpressionUtils.allOf(predicate, QHotel.hotel.isEnabled.isTrue()), pageable)
				.map(this::buildHotelDetailResponse);
	}

	@Override
	public Page<HotelDetailResponse> findAllHotel(final Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QHotel.hotel);
		log.trace("Finding hotels predicate [{}]", predicate);
		return hotelService.findAll(ExpressionUtils.allOf(predicate, QHotel.hotel.isEnabled.isTrue()), pageable)
				.map(this::buildHotelDetailResponse);
	}

	private HotelDetailResponse buildHotelDetailResponse(final Hotel hotel) {
		HotelDetailResponse response = new HotelDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Hotel, HotelDetailResponse> typeMap = modelMapper.typeMap(Hotel.class, HotelDetailResponse.class);
		typeMap.map(hotel, response);

		final List<HotelFacilityDto> hotelFacilities = new ArrayList<HotelFacilityDto>();
		List<HotelFacility> hotelFacilityLst = hotelFacilityService
				.findAll(QHotelFacility.hotelFacility.hotel.eq(hotel), Pageable.unpaged()).getContent();
		hotelFacilityLst.forEach(hotelFacility -> {
			final HotelFacility facility = hotelFacility;
			HotelFacilityDto hotelFacilityResponse = new HotelFacilityDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<HotelFacility, HotelFacilityDto> type = modelMapper.typeMap(HotelFacility.class,
					HotelFacilityDto.class);
			type.map(facility, hotelFacilityResponse);
			hotelFacilities.add(hotelFacilityResponse);
		});

		response.setHotelFacilityLst(hotelFacilities);

		final List<RoomTypeDto> RoomTypes = new ArrayList<RoomTypeDto>();
		List<RoomType> roomTypeLst = roomTypeService.findAll(QRoomType.roomType.hotel.eq(hotel), Pageable.unpaged())
				.getContent();
		roomTypeLst.forEach(roomType -> {
			final RoomType hotelRoomType = roomType;
			RoomTypeDto roomTypeResponse = new RoomTypeDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<RoomType, RoomTypeDto> type = modelMapper.typeMap(RoomType.class, RoomTypeDto.class);
			type.map(hotelRoomType, roomTypeResponse);
			RoomTypes.add(roomTypeResponse);
		});

		final List<RestaurantMenuDto> menuLst = new ArrayList<RestaurantMenuDto>();
		List<RestaurantMenu> restaurantMenuLst = restaurantMenuService
				.findAll(QRestaurantMenu.restaurantMenu.hotel.eq(hotel), Pageable.unpaged()).getContent();
		restaurantMenuLst.forEach(menu -> {
			final RestaurantMenu restaurantMenu = menu;
			RestaurantMenuDto restaurantResponse = new RestaurantMenuDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<RestaurantMenu, RestaurantMenuDto> type = modelMapper.typeMap(RestaurantMenu.class,
					RestaurantMenuDto.class);
			type.map(restaurantMenu, restaurantResponse);
			menuLst.add(restaurantResponse);
		});

		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.hotel.eq(hotel), Pageable.unpaged()).getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
		response.setReviewLst(reviewLst);

//		response.setReviewLst(reviewService.findAll(QReview.review1.hotel.eq(hotel), Pageable.unpaged()).getContent());
		response.setRoomType(RoomTypes);
		response.setFoodMenu(menuLst);
		return response;
	}

	private Hotel buildHotel(final HotelCreateRequest request) {
		Hotel hotel = new Hotel();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<HotelCreateRequest, Hotel> typeMap = modelMapper.typeMap(HotelCreateRequest.class, Hotel.class);
		typeMap.addMappings(mapper -> mapper.skip(Hotel::setHotelId));
		typeMap.addMappings(mapper -> mapper.skip(Hotel::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Hotel::setLastUpdated));
		typeMap.map(request, hotel);
		if (request.getImages() != null && request.getImages().size() > 0)
			hotel.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));
//		else {
//			if (request.getBusinessType().equalsIgnoreCase("hotel"))
//				hotel.setImages("hotel.jpg");
//			else
//				hotel.setImages("restaurant.jpeg");
//		}

		return hotel;
	}

	private Hotel buildHotel(Hotel hotel, final HotelUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<HotelUpdateRequest, Hotel> typeMap = modelMapper.typeMap(HotelUpdateRequest.class, Hotel.class);
		typeMap.addMappings(mapper -> mapper.skip(Hotel::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(Hotel::setLastUpdated));
		typeMap.map(request, hotel);
		if (request.getImages() != null && request.getImages().size() > 0)
			hotel.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));
//		else {
//			if (request.getBusinessType().equalsIgnoreCase("hotel"))
//				hotel.setImages("hotel.jpg");
//			else
//				hotel.setImages("restaurant.jpeg");
//		}

		return hotel;
	}

	private RestaurantMenu buildRestaurantMenu(final RestaurantMenuDto request, Hotel savedHotel) {
		RestaurantMenu restaurantMenu = new RestaurantMenu();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<RestaurantMenuDto, RestaurantMenu> typeMap = modelMapper.typeMap(RestaurantMenuDto.class,
				RestaurantMenu.class);
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setMenuId));
//		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RestaurantMenu::setLastUpdated));
		typeMap.map(request, restaurantMenu);
		restaurantMenu.setHotel(savedHotel);
		return restaurantMenu;
	}

	private RoomType buildRoomType(final RoomTypeDto request, Hotel savedHotel) {
		RoomType roomType = new RoomType();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<RoomTypeDto, RoomType> typeMap = modelMapper.typeMap(RoomTypeDto.class, RoomType.class);
		typeMap.addMappings(mapper -> mapper.skip(RoomType::setRoomTypeId));
		typeMap.addMappings(mapper -> mapper.skip(RoomType::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(RoomType::setLastUpdated));
		typeMap.map(request, roomType);
		roomType.setHotel(savedHotel);
		return roomType;
	}

	private HotelFacility buildHotelFacility(HotelFacilityDto request, Hotel savedHotel) {
		HotelFacility hotelFacility = new HotelFacility();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<HotelFacilityDto, HotelFacility> typeMap = modelMapper.typeMap(HotelFacilityDto.class,
				HotelFacility.class);
		typeMap.addMappings(mapper -> mapper.skip(HotelFacility::setHotelFacilityId));
		typeMap.addMappings(mapper -> mapper.skip(HotelFacility::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(HotelFacility::setLastUpdated));
		typeMap.map(request, hotelFacility);
		hotelFacility.setHotel(savedHotel);
		return hotelFacility;
	}

}
