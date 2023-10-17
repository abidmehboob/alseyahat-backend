package com.alseyahat.app.feature.wallet.service.impl;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.wallet.repository.TransactionTypeRepository;
import com.alseyahat.app.feature.wallet.repository.entity.TransactionType;
import com.alseyahat.app.feature.wallet.service.TransactionTypeService;
import com.querydsl.core.types.Predicate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionTypeServiceImpl implements TransactionTypeService {

	TransactionTypeRepository transactionTypeRepository;
	
	@Override
	public TransactionType save(TransactionType entity) {
		return transactionTypeRepository.save(entity);
	}

	@Override
	public TransactionType findOne(Predicate predicate) {
		return transactionTypeRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.transactionType_entity_not_found"));
	}

	@Override
	public Optional<TransactionType> find_One(Predicate predicate) {
		return transactionTypeRepository.findOne(predicate);
	}

	@Override
	public Page<TransactionType> findAll(Predicate predicate, Pageable pageable) {
		return transactionTypeRepository.findAll(predicate, pageable);
	}

}

