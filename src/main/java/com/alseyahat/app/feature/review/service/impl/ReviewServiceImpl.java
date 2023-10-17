package com.alseyahat.app.feature.review.service.impl;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.review.repository.ReviewRepository;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.querydsl.core.types.Predicate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewServiceImpl implements ReviewService {

	ReviewRepository reviewRepository;
	
	@Override
	public Review save(Review entity) {
		return reviewRepository.save(entity);
	}

	@Override
	public Review findOne(Predicate predicate) {
		return reviewRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.store_entity_not_found"));
	}

	@Override
	public Optional<Review> find_One(Predicate predicate) {
		return reviewRepository.findOne(predicate);
	}

	@Override
	public Page<Review> findAll(Predicate predicate, Pageable pageable) {
		return reviewRepository.findAll(predicate, pageable);
	}
	
	@Override
	public Iterator<Review> findAllReviews(Predicate predicate, Pageable pageable) {
		return reviewRepository.findAll(predicate, pageable).iterator();
	}


}