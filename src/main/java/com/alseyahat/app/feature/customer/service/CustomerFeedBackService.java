package com.alseyahat.app.feature.customer.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.customer.repository.entity.CustomerFeedBack;
import com.querydsl.core.types.Predicate;

public interface CustomerFeedBackService {

	CustomerFeedBack save(final CustomerFeedBack entity);

	CustomerFeedBack findOne(final Predicate predicate);
    
    Optional<CustomerFeedBack> find_One(Predicate predicate);

    Page<CustomerFeedBack> findAll(final Predicate predicate, final Pageable pageable);

}