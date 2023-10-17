package com.alseyahat.app.feature.review.facade;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.feature.review.dto.ReviewCreateRequest;
import com.alseyahat.app.feature.review.dto.ReviewCreateResponse;
import com.alseyahat.app.feature.review.dto.ReviewDetailResponse;
import com.alseyahat.app.feature.review.dto.ReviewUpdateRequest;
import com.alseyahat.app.feature.review.dto.ReviewUpdateResponse;

public interface ReviewFacade {
	
	void deleteReview(final String reviewId);

	ReviewDetailResponse findReviewId(final String reviewId);

	ReviewCreateResponse createReview(final ReviewCreateRequest request);

	ReviewUpdateResponse updateReview(final ReviewUpdateRequest request);

	Page<ReviewDetailResponse> findAllReview( Map<String, Object> parameters, final Pageable pageable);
}
