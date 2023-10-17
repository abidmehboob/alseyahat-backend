package com.alseyahat.app.feature.customer.service.impl;

import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.customer.repository.CustomerFeedBackRepository;
import com.alseyahat.app.feature.customer.repository.entity.CustomerFeedBack;
import com.alseyahat.app.feature.customer.service.CustomerFeedBackService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerFeedBackServiceImpl implements CustomerFeedBackService {

	CustomerFeedBackRepository customerFeedBackRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerFeedBack save(final CustomerFeedBack entity) {
        return customerFeedBackRepository.save(entity);
    }

    @Override
    public CustomerFeedBack findOne(Predicate predicate) {
        return customerFeedBackRepository.findOne(predicate)
                .orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.customer_feed_back_entity_not_found"));
    }
    
    @Override
    public Optional<CustomerFeedBack> find_One(Predicate predicate) {
        return customerFeedBackRepository.findOne(predicate);
    }

    @Override
    public Page<CustomerFeedBack> findAll(Predicate predicate, Pageable pageable) {
        return customerFeedBackRepository.findAll(predicate, pageable);
    }
}
