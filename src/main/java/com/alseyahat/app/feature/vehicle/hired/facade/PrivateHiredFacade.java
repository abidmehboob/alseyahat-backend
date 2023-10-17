package com.alseyahat.app.feature.vehicle.hired.facade;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredCreateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredCreateResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredUpdateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface PrivateHiredFacade {

	void deletePrivateHired(final String privateHiredId);

	PrivateHiredDetailResponse findPrivateHiredId(final String privateHiredId);

	PrivateHiredCreateResponse createPrivateHired(final PrivateHiredCreateRequest request);

	PrivateHiredUpdateResponse updatePrivateHired(final PrivateHiredUpdateRequest request);

	Page<PrivateHiredDetailResponse> findAllPrivateHired(final Predicate predicate, final Pageable pageable);
	
	Page<PrivateHiredDetailResponse> findAllPrivateHired(@RequestParam final Map<String, Object> parameters, final Pageable pageable);
	
}