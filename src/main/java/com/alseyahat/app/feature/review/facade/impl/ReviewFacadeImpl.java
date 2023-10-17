package com.alseyahat.app.feature.review.facade.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.common.constant.Constants;
import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.deal.repository.entity.Deal;
import com.alseyahat.app.feature.deal.repository.entity.QDeal;
import com.alseyahat.app.feature.deal.service.DealService;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.hotel.repository.entity.QHotel;
import com.alseyahat.app.feature.hotel.service.HotelService;
import com.alseyahat.app.feature.review.dto.DealDetailResponse;
import com.alseyahat.app.feature.review.dto.HotelDetailResponse;
import com.alseyahat.app.feature.review.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.review.dto.ReviewCreateRequest;
import com.alseyahat.app.feature.review.dto.ReviewCreateResponse;
import com.alseyahat.app.feature.review.dto.ReviewDetailResponse;
import com.alseyahat.app.feature.review.dto.ReviewUpdateRequest;
import com.alseyahat.app.feature.review.dto.ReviewUpdateResponse;
import com.alseyahat.app.feature.review.dto.SightSeeingDetailResponse;
import com.alseyahat.app.feature.review.facade.ReviewFacade;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.alseyahat.app.feature.sightSeeing.repository.entity.QSightSeeing;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeing;
import com.alseyahat.app.feature.sightSeeing.service.SightSeeingService;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHired;
import com.alseyahat.app.feature.vehicle.hired.service.PrivateHiredService;
import com.querydsl.core.types.Predicate;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewFacadeImpl implements ReviewFacade {

	ReviewService reviewService;
	ModelMapper modelMapper;
	HotelService hotelService;
	PrivateHiredService privateHiredService;
	SightSeeingService sightSeeingService;
	DealService dealService;
	CustomerService customerService;

	@Override
	public void deleteReview(String reviewId) {
		Review review = reviewService.findOne(QReview.review1.reviewRatingId.eq(reviewId));
		if (review.isEnabled())
			review.setEnabled(Boolean.FALSE);
		else
			review.setEnabled(Boolean.TRUE);

		reviewService.save(review);
		log.trace("Review deleted with id [{}]", reviewId);
	}

	@Override
	public ReviewDetailResponse findReviewId(String reviewId) {
		Review review = reviewService.findOne(QReview.review1.reviewRatingId.eq(reviewId));
		return buildReviewDetailResponse(review);
	}

	@Override
	public ReviewCreateResponse createReview(ReviewCreateRequest request) {
		final Review savedReview = reviewService.save(buildReview(request));
		
		 if (request.getHotelId()!=null && !request.getHotelId().equals("") ) {
	          
	            Hotel hotel = hotelService.findOne(QHotel.hotel.hotelId.eq(request.getHotelId()));
	            hotel.setItemAverageRating(getAverageRating(request.getHotelId(), null,null,null).getAverageRating());
	            hotelService.save(hotel);
	        } else if (request.getSightSeeingId()!=null && !request.getSightSeeingId().equals("") ) {
	          
	            SightSeeing sightSeeing = sightSeeingService.findOne(QSightSeeing.sightSeeing.sightSeeingId.eq(request.getSightSeeingId()));
	            sightSeeing.setSightAverageRating(getAverageRating(null, request.getSightSeeingId(),null,null).getAverageRating());
	            sightSeeingService.save(sightSeeing);
	        }  else if (request.getDealId()!=null && !request.getDealId().equals("") ) {
	          
	            Deal deal = dealService.findOne(QDeal.deal.dealId.eq(request.getDealId()));
	            deal.setDealAverageRating(getAverageRating(null, null,request.getDealId(),null).getAverageRating());
	            dealService.save(deal);
	        }  else if (request.getPrivateHiredId()!=null && !request.getPrivateHiredId().equals("") ) {
	          
	        	PrivateHired privateHired = privateHiredService.findOne(QPrivateHired.privateHired.privateHiredId.eq(request.getPrivateHiredId()));
	        	privateHired.setPrivateHiredAverageRating(getAverageRating(null, null,null,request.getPrivateHiredId()).getAverageRating());
	        	privateHiredService.save(privateHired);
	        } 
		
		return modelMapper.map(savedReview, ReviewCreateResponse.class);
//		return ReviewCreateResponse.builder().ReviewRatingId(savedReview.getReviewRatingId()).build();
	}

	@Override
	public ReviewUpdateResponse updateReview(ReviewUpdateRequest request) {
		Review review = reviewService.findOne(QReview.review1.reviewRatingId.eq(request.getReviewRatingId()));
		final Review savedReview = reviewService.save(buildReview(review, request));
		return ReviewUpdateResponse.builder().ReviewRatingId(savedReview.getReviewRatingId()).build();
	}

	@Override
	public Page<ReviewDetailResponse> findAllReview(Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QReview.review1);
		return reviewService.findAll(predicate, pageable).map(this::buildReviewDetailResponse);
	}

	private ReviewDetailResponse buildReviewDetailResponse(final Review review) {
		ReviewDetailResponse response = new ReviewDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Review, ReviewDetailResponse> typeMap = modelMapper.typeMap(Review.class, ReviewDetailResponse.class);
		typeMap.map(review, response);

		if (review.getHotel() != null) {
			Optional<Hotel> hotel = hotelService.find_One(QHotel.hotel.hotelId.eq(review.getHotel().getHotelId()));
			if (hotel.isPresent())
				response.setHotel(buildHotelDetailResponse(hotel.get()));
		} else if (review.getSightSeeing() != null) {
			Optional<SightSeeing> sightSeeing = sightSeeingService
					.find_One(QSightSeeing.sightSeeing.sightSeeingId.eq(review.getSightSeeing().getSightSeeingId()));
			if (sightSeeing.isPresent())
				response.setSightSeeing(buildSightSeeingDetailResponse(sightSeeing.get()));
		} else if (review.getPrivateHired() != null) {
			Optional<PrivateHired> privateHired = privateHiredService.find_One(
					QPrivateHired.privateHired.privateHiredId.eq(review.getPrivateHired().getPrivateHiredId()));
			if (privateHired.isPresent())
				response.setPrivateHired(buildPrivateHiredDetailResponse(privateHired.get()));
		} else if (review.getDeal() != null) {
			Optional<Deal> deal = dealService.find_One(QDeal.deal.dealId.eq(review.getDeal().getDealId()));
			if (deal.isPresent())
				response.setDeal(buildDealDetailResponse(deal.get()));
		}

		return response;
	}

	private Review buildReview(final ReviewCreateRequest request) {
		Review review = new Review();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<ReviewCreateRequest, Review> typeMap = modelMapper.typeMap(ReviewCreateRequest.class,
				Review.class);
		typeMap.addMappings(mapper -> mapper.skip(Review::setReviewRatingId));
		typeMap.addMappings(mapper -> mapper.skip(Review::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Review::setLastUpdated));
		typeMap.map(request, review);
		 String username = AppUtils.getUserNameFromAuthentication();
		 review.setCustomer(customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username))));
	        
		if (request.getHotelId() != null && !request.getHotelId().isEmpty())
			review.setHotel(hotelService
					.findOne(QHotel.hotel.hotelId.eq(request.getHotelId()).and(QHotel.hotel.isEnabled.isTrue())));
		if (request.getPrivateHiredId() != null && !request.getPrivateHiredId().isEmpty())
			review.setPrivateHired(privateHiredService.findOne(QPrivateHired.privateHired.privateHiredId
					.eq(request.getPrivateHiredId()).and(QPrivateHired.privateHired.isEnabled.isTrue())));
		if (request.getSightSeeingId() != null && !request.getSightSeeingId().isEmpty())
			review.setSightSeeing(sightSeeingService.findOne(QSightSeeing.sightSeeing.sightSeeingId
					.eq(request.getSightSeeingId()).and(QSightSeeing.sightSeeing.isEnabled.isTrue())));
		if (request.getDealId() != null && !request.getDealId().isEmpty())
			review.setDeal(
					dealService.findOne(QDeal.deal.dealId.eq(request.getDealId()).and(QDeal.deal.isEnabled.isTrue())));
//        category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return review;
	}

	private SightSeeingDetailResponse buildSightSeeingDetailResponse(final SightSeeing sightSeeing) {
		SightSeeingDetailResponse response = new SightSeeingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeing, SightSeeingDetailResponse> typeMap = modelMapper.typeMap(SightSeeing.class,
				SightSeeingDetailResponse.class);
		typeMap.map(sightSeeing, response);
		return response;
	}

	private PrivateHiredDetailResponse buildPrivateHiredDetailResponse(final PrivateHired privateHired) {
		PrivateHiredDetailResponse response = new PrivateHiredDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<PrivateHired, PrivateHiredDetailResponse> typeMap = modelMapper.typeMap(PrivateHired.class,
				PrivateHiredDetailResponse.class);
		typeMap.map(privateHired, response);

		return response;
	}

	private HotelDetailResponse buildHotelDetailResponse(final Hotel hotel) {
		HotelDetailResponse response = new HotelDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Hotel, HotelDetailResponse> typeMap = modelMapper.typeMap(Hotel.class, HotelDetailResponse.class);
		typeMap.map(hotel, response);
		return response;
	}

	private DealDetailResponse buildDealDetailResponse(final Deal deal) {
		DealDetailResponse response = new DealDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Deal, DealDetailResponse> typeMap = modelMapper.typeMap(Deal.class, DealDetailResponse.class);
		typeMap.map(deal, response);

		return response;
	}

	private Review buildReview(Review review, final ReviewUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<ReviewUpdateRequest, Review> typeMap = modelMapper.typeMap(ReviewUpdateRequest.class, Review.class);
		typeMap.addMappings(mapper -> mapper.skip(Review::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Review::setLastUpdated));
		typeMap.map(request, review);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return review;
	}
	
	 public ReviewCreateResponse getAverageRating(String hotelId, String sightSeeingId , String dealId , String privateHiredId) {
	        Iterator<Review> reviewRatings = null;
	        int totalReviews = 0;
	        int totalRatings = 0;
	        int rating1 = 0;
	        int rating2 = 0;
	        int rating3 = 0;
	        int rating4 = 0;
	        int rating5 = 0;

	        if (Objects.nonNull(hotelId)) {
	            reviewRatings =  reviewService.findAllReviews(
	            		QReview.review1.hotel.hotelId.eq(hotelId),Pageable.unpaged());
	        } else if (!Objects.isNull(sightSeeingId)) {
	            reviewRatings = reviewService.findAllReviews(
	            		QReview.review1.sightSeeing.sightSeeingId.eq(sightSeeingId),Pageable.unpaged());
	        } else if (!Objects.isNull(dealId)) {
	            reviewRatings = reviewService.findAllReviews(
	            		QReview.review1.deal.dealId.eq(dealId),Pageable.unpaged());
	        } else if (!Objects.isNull(privateHiredId)) {
	            reviewRatings =  reviewService.findAllReviews(
	            		QReview.review1.privateHired.privateHiredId.eq(privateHiredId),Pageable.unpaged());
	        }
	        while (reviewRatings.hasNext()) {
	        	Review next = reviewRatings.next();
	            totalRatings = totalRatings + next.getRating();
	            totalReviews++;
	            if(next.getRating().equals(1)) {
	            	rating1++;
	            }
	            if(next.getRating().equals(2)) {
	            	rating2++;
	            }
	            if(next.getRating().equals(3)) {
	            	rating3++;
	            }
	            if(next.getRating().equals(4)) {
	            	rating4++;
	            }
	            if(next.getRating().equals(5)) {
	            	rating5++;
	            }
	        }
	        
	        
	        ReviewCreateResponse averageRatingResponse = new ReviewCreateResponse();
//	        averageRatingResponse.setBranchId(branchId);
//	        averageRatingResponse.setItemId(itemId);
	        averageRatingResponse.setAverageRating(totalReviews == 0 ? Constants.AVG_RATING_IF_REVIEWS_ZERO : (double) totalRatings / (double) totalReviews);
	        averageRatingResponse.setTotalReviews(totalReviews);
	        averageRatingResponse.setRating1(rating1);
	        averageRatingResponse.setRating2(rating2);
	        averageRatingResponse.setRating3(rating3);
	        averageRatingResponse.setRating4(rating4);
	        averageRatingResponse.setRating5(rating5);
	        return averageRatingResponse;
	    }
	

}
