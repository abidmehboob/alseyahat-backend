package com.alseyahat.app.feature.wallet.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.wallet.repository.entity.Currency;
import com.querydsl.core.types.Predicate;

public interface CurrencyService {

	Currency save(final Currency entity);

	Currency findOne(final Predicate predicate);

    Optional<Currency> find_One(final Predicate predicate);

    Page<Currency> findAll(final Predicate predicate, final Pageable pageable);

}
