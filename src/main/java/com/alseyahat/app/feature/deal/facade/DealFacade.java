package com.alseyahat.app.feature.deal.facade;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import com.alseyahat.app.feature.deal.dto.DealCreateRequest;
import com.alseyahat.app.feature.deal.dto.DealCreateResponse;
import com.alseyahat.app.feature.deal.dto.DealDetailResponse;
import com.alseyahat.app.feature.deal.dto.DealUpdateRequest;
import com.alseyahat.app.feature.deal.dto.DealUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface DealFacade {
	
	void deleteDeal(final String dealId);

	DealDetailResponse findDealId(final String dealId);

	DealCreateResponse createDeal(final DealCreateRequest request);

	DealUpdateResponse updateDeal(final DealUpdateRequest request);

	Page<DealDetailResponse> findAllDeal(final Predicate predicate, final Pageable pageable);
	
	Page<DealDetailResponse> findAllDeal(@RequestParam final Map<String, Object> parameters, final Pageable pageable);
	
	
}