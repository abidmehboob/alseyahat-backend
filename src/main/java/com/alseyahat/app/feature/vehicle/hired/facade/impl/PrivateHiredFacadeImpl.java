package com.alseyahat.app.feature.vehicle.hired.facade.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredCreateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredCreateResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredUpdateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredUpdateResponse;
import com.alseyahat.app.feature.vehicle.hired.facade.PrivateHiredFacade;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHired;
import com.alseyahat.app.feature.vehicle.hired.service.PrivateHiredService;
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
public class PrivateHiredFacadeImpl implements PrivateHiredFacade {

	PrivateHiredService privateHiredService;
	ModelMapper modelMapper;
	ReviewService reviewService;

	@Override
	public void deletePrivateHired(String privateHiredId) {
		PrivateHired privateHired = privateHiredService
				.findOne(QPrivateHired.privateHired.privateHiredId.eq(privateHiredId));
		if (privateHired.isEnabled())
			privateHired.setEnabled(Boolean.FALSE);
		else
			privateHired.setEnabled(Boolean.TRUE);

		privateHiredService.save(privateHired);
		log.trace("PrivateHired deleted with id [{}]", privateHiredId);
	}

	@Override
	public PrivateHiredDetailResponse findPrivateHiredId(String privateHiredId) {
		PrivateHired privateHired = privateHiredService.findOne(QPrivateHired.privateHired.privateHiredId
				.eq(privateHiredId).and(QPrivateHired.privateHired.isEnabled.isTrue()));
		return buildPrivateHiredDetailResponse(privateHired);
	}

	@Override
	public PrivateHiredCreateResponse createPrivateHired(PrivateHiredCreateRequest request) {
		final PrivateHired privateHired = privateHiredService.save(buildPrivateHired(request));
		return PrivateHiredCreateResponse.builder().privateHiredId(privateHired.getPrivateHiredId()).build();
	}

	@Override
	public PrivateHiredUpdateResponse updatePrivateHired(PrivateHiredUpdateRequest request) {
		PrivateHired privateHired = privateHiredService
				.findOne(QPrivateHired.privateHired.privateHiredId.eq(request.getPrivateHiredId()));
		final PrivateHired savedPrivateHired = privateHiredService.save(buildPrivateHired(privateHired, request));
		return PrivateHiredUpdateResponse.builder().privateHiredId(savedPrivateHired.getPrivateHiredId()).build();
	}

	@Override
	public Page<PrivateHiredDetailResponse> findAllPrivateHired(Predicate predicate, Pageable pageable) {
		return privateHiredService
				.findAll(ExpressionUtils.allOf(predicate, QPrivateHired.privateHired.isEnabled.isTrue()), pageable)
				.map(this::buildPrivateHiredDetailResponse);
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

		response.setReviewLst(reviewLst);
//		response.setReviewLst(reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged()).getContent());
		return response;
	}

	private PrivateHired buildPrivateHired(final PrivateHiredCreateRequest request) {
		PrivateHired privateHired = new PrivateHired();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<PrivateHiredCreateRequest, PrivateHired> typeMap = modelMapper
				.typeMap(PrivateHiredCreateRequest.class, PrivateHired.class);
		typeMap.addMappings(mapper -> mapper.skip(PrivateHired::setPrivateHiredId));
		typeMap.addMappings(mapper -> mapper.skip(PrivateHired::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(PrivateHired::setLastUpdated));
		typeMap.map(request, privateHired);
		if (privateHired.getImages() != null && privateHired.getImages().length() > 0)
			privateHired.setImages(
					request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));
		else
			privateHired.setImages("private_hired.png");

		return privateHired;
	}

	private PrivateHired buildPrivateHired(PrivateHired privateHired, final PrivateHiredUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<PrivateHiredUpdateRequest, PrivateHired> typeMap = modelMapper.typeMap(PrivateHiredUpdateRequest.class,
				PrivateHired.class);
//		typeMap.addMappings(mapper -> mapper.skip(PrivateHired::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(PrivateHired::setLastUpdated));
		typeMap.map(request, privateHired);
		if (request.getImages() != null && request.getImages().size() > 0)
			privateHired.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

//		else {
//			if(privateHired.getImages() == null || privateHired.getImages().length() == 0)
//				privateHired.setImages("private_hired.png");
//		}
			
		return privateHired;
	}

	@Override
	public Page<PrivateHiredDetailResponse> findAllPrivateHired(Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QPrivateHired.privateHired);
		log.trace("Finding PrivateHired predicate [{}]", predicate);
		return privateHiredService
				.findAll(ExpressionUtils.allOf(predicate, QPrivateHired.privateHired.isEnabled.isTrue()), pageable)
				.map(this::buildPrivateHiredDetailResponse);
	}

}
