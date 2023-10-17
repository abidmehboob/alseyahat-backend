package com.alseyahat.app.feature.deal.facade.impl;

import static com.alseyahat.app.commons.Constants.SEPARATOR;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.feature.deal.dto.DealCreateRequest;
import com.alseyahat.app.feature.deal.dto.DealCreateResponse;
import com.alseyahat.app.feature.deal.dto.DealDetailResponse;
import com.alseyahat.app.feature.deal.dto.DealUpdateRequest;
import com.alseyahat.app.feature.deal.dto.DealUpdateResponse;
import com.alseyahat.app.feature.deal.facade.DealFacade;
import com.alseyahat.app.feature.deal.repository.entity.Deal;
import com.alseyahat.app.feature.deal.repository.entity.QDeal;
import com.alseyahat.app.feature.deal.service.DealService;
import com.alseyahat.app.feature.hotel.dto.HotelDetailResponse;
import com.alseyahat.app.feature.hotel.dto.HotelFacilityDto;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.hotel.repository.entity.HotelFacility;
import com.alseyahat.app.feature.hotel.repository.entity.QHotel;
import com.alseyahat.app.feature.hotel.repository.entity.QHotelFacility;
import com.alseyahat.app.feature.hotel.service.HotelFacilityService;
import com.alseyahat.app.feature.hotel.service.HotelService;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingDetailResponse;
import com.alseyahat.app.feature.sightSeeing.repository.entity.QSightSeeing;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeing;
import com.alseyahat.app.feature.sightSeeing.service.SightSeeingService;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHired;
import com.alseyahat.app.feature.vehicle.hired.service.PrivateHiredService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DealFacadeImpl implements DealFacade {

	DealService dealService;
	ModelMapper modelMapper;
	ReviewService reviewService;
	HotelService hotelService;
	HotelFacilityService hotelFacilityService;
	SightSeeingService sightSeeingService;
	PrivateHiredService privateHiredService;

	@Override
	public void deleteDeal(String dealId) {

		Deal deal = dealService.findOne(QDeal.deal.dealId.eq(dealId));
		if (deal.isEnabled())
			deal.setEnabled(Boolean.FALSE);
		else
			deal.setEnabled(Boolean.TRUE);

		dealService.save(deal);
		log.trace("Deal deleted with id [{}]", dealId);
	}

	@Override
	public DealDetailResponse findDealId(String dealId) {
		Deal deal = dealService.findOne(QDeal.deal.dealId.eq(dealId).and(QDeal.deal.isEnabled.isTrue()));
		return buildDealDetailResponse(deal);
	}

	@Override
	public DealCreateResponse createDeal(DealCreateRequest request) {
		final Deal savedDeal = dealService.save(buildDeal(request));
		return DealCreateResponse.builder().dealId(savedDeal.getDealId()).build();
	}

	@Override
	public DealUpdateResponse updateDeal(DealUpdateRequest request) {
		Deal deal = dealService.findOne(QDeal.deal.dealId.eq(request.getDealId()));
		final Deal updatedDeal = dealService.save(buildDeal(deal, request));
		return DealUpdateResponse.builder().dealId(updatedDeal.getDealId()).build();
	}

	@Override
	public Page<DealDetailResponse> findAllDeal(Predicate predicate, Pageable pageable) {
		return dealService.findAll(ExpressionUtils.allOf(predicate, QDeal.deal.isEnabled.isTrue()), pageable)
				.map(this::buildDealDetailResponse);
	}

	private DealDetailResponse buildDealDetailResponse(final Deal deal) {
		DealDetailResponse response = new DealDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Deal, DealDetailResponse> typeMap = modelMapper.typeMap(Deal.class, DealDetailResponse.class);
		typeMap.map(deal, response);
		if (deal.getHotelId() != null && !deal.getHotelId().equals("")) {
			Optional<Hotel> hotel = hotelService.find_One(QHotel.hotel.hotelId.eq(deal.getHotelId()));
			if (hotel.isPresent())
				response.setHotelDetailResponse(buildHotelDetailResponse(hotel.get()));
		}

		if (deal.getSightSeeingId() != null && !deal.getSightSeeingId().equals("")) {
			Optional<SightSeeing> sightSeeing = sightSeeingService
					.find_One(QSightSeeing.sightSeeing.sightSeeingId.eq(deal.getSightSeeingId()));
			if (sightSeeing.isPresent())
				response.setSightSeeingDetailResponse(buildSightSeeingDetailResponse(sightSeeing.get()));
		}

		if (deal.getPrivateHiredId() != null && !deal.getPrivateHiredId().equals("")) {
			Optional<PrivateHired> privateHired = privateHiredService
					.find_One(QPrivateHired.privateHired.privateHiredId.eq(deal.getPrivateHiredId()));
			if (privateHired.isPresent())
				response.setPrivateHiredDetailResponse(buildPrivateHiredDetailResponse(privateHired.get()));
		}

		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.deal.eq(deal), Pageable.unpaged()).getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});

		response.setReviewLst(reviewLst);
//		response.setReviewLst(reviewService.findAll(QReview.review1.deal.eq(deal), Pageable.unpaged()).getContent());
		return response;
	}

	private HotelDetailResponse buildHotelDetailResponse(final Hotel hotel) {
		HotelDetailResponse response = new HotelDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Hotel, HotelDetailResponse> typeMap = modelMapper.typeMap(Hotel.class, HotelDetailResponse.class);
		typeMap.map(hotel, response);
//		List<HotelFacilityDto> hotelFacilityDtoLst  =(List<HotelFacilityDto>) hotelFacilityService.findAll(QHotelFacility.hotelFacility.hotel.eq(hotel), Pageable.unpaged()).map(this::buildHotelFacilityDetailResponse);

		final List<HotelFacilityDto> hotelFacilityDtoLst = new ArrayList<HotelFacilityDto>();
		List<HotelFacility> hotelFacilityLst = hotelFacilityService
				.findAll(QHotelFacility.hotelFacility.hotel.eq(hotel), Pageable.unpaged()).getContent();
		hotelFacilityLst.forEach(hotelFacility -> {
			final HotelFacility facility = hotelFacility;
			HotelFacilityDto hotelFacilityResponse = new HotelFacilityDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<HotelFacility, HotelFacilityDto> type = modelMapper.typeMap(HotelFacility.class,
					HotelFacilityDto.class);
			type.map(facility, hotelFacilityResponse);
			hotelFacilityDtoLst.add(hotelFacilityResponse);
		});

		response.setHotelFacilityLst(hotelFacilityDtoLst);
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
		return response;
	}

	private Deal buildDeal(final DealCreateRequest request) {
		Deal deal = new Deal();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<DealCreateRequest, Deal> typeMap = modelMapper.typeMap(DealCreateRequest.class, Deal.class);
		typeMap.addMappings(mapper -> mapper.skip(Deal::setDealId));
		typeMap.addMappings(mapper -> mapper.skip(Deal::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(Deal::setLastUpdated));
		typeMap.map(request, deal);
		if (request.getImages() != null && request.getImages().size() > 0)
			deal.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));
		else
			deal.setImages("deal.jpg");

		return deal;
	}

	private Deal buildDeal(Deal deal, final DealUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<DealUpdateRequest, Deal> typeMap = modelMapper.typeMap(DealUpdateRequest.class, Deal.class);
//		typeMap.addMappings(mapper -> mapper.skip(Deal::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Deal::setLastUpdated));
		typeMap.map(request, deal);

		if (request.getImages() != null && request.getImages().size() > 0)
			deal.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));
//		else
//			deal.setImages("deal.jpg");

		return deal;
	}

	private SightSeeingDetailResponse buildSightSeeingDetailResponse(final SightSeeing sightSeeing) {
		SightSeeingDetailResponse response = new SightSeeingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeing, SightSeeingDetailResponse> typeMap = modelMapper.typeMap(SightSeeing.class,
				SightSeeingDetailResponse.class);
		typeMap.map(sightSeeing, response);
		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.sightSeeing.eq(sightSeeing), Pageable.unpaged())
				.getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
		response.setReviewLst(reviewLst);
//		response.setReviewLst(reviewService.findAll(QReview.review1.sightSeeing.eq(sightSeeing), Pageable.unpaged()).getContent());
		return response;
	}

	private PrivateHiredDetailResponse buildPrivateHiredDetailResponse(final PrivateHired privateHired) {
		PrivateHiredDetailResponse response = new PrivateHiredDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<PrivateHired, PrivateHiredDetailResponse> typeMap = modelMapper.typeMap(PrivateHired.class,
				PrivateHiredDetailResponse.class);
		typeMap.map(privateHired, response);
		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged())
				.getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
//		response.setReviewLst(reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged()).getContent());

		return response;
	}

	@Override
	public Page<DealDetailResponse> findAllDeal(Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QDeal.deal);
		log.trace("Finding deals predicate [{}]", predicate);
		return dealService.findAll(ExpressionUtils.allOf(predicate, QDeal.deal.isEnabled.isTrue()), pageable)
				.map(this::buildDealDetailResponse);
	}
}
