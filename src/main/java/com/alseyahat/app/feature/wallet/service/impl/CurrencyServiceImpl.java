package com.alseyahat.app.feature.wallet.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.wallet.repository.CurrencyRepository;
import com.alseyahat.app.feature.wallet.repository.entity.Currency;
import com.alseyahat.app.feature.wallet.service.CurrencyService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CurrencyServiceImpl implements CurrencyService {

	CurrencyRepository currencyRepository;
	
	@Override
	public Currency save(Currency entity) {
		return currencyRepository.save(entity);
	}

	@Override
	public Currency findOne(Predicate predicate) {
		return currencyRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.currency_entity_not_found"));
	}

	@Override
	public Optional<Currency> find_One(Predicate predicate) {
		return currencyRepository.findOne(predicate);
	}

	@Override
	public Page<Currency> findAll(Predicate predicate, Pageable pageable) {
		return currencyRepository.findAll(predicate, pageable);
	}

}

