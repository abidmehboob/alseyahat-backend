package com.alseyahat.app.feature.wallet.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.wallet.repository.TransactionRepository;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;
import com.alseyahat.app.feature.wallet.service.TransactionService;
import com.querydsl.core.types.Predicate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionServiceImpl implements TransactionService {

	TransactionRepository transactionRepository;
	
	@Override
	public Transaction save(Transaction entity) {
		return transactionRepository.save(entity);
	}

	@Override
	public Transaction findOne(Predicate predicate) {
		return transactionRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.transaction_entity_not_found"));
	}

	@Override
	public Optional<Transaction> find_One(Predicate predicate) {
		return transactionRepository.findOne(predicate);
	}

	@Override
	public Page<Transaction> findAll(Predicate predicate, Pageable pageable) {
		return transactionRepository.findAll(predicate, pageable);
	}

}

