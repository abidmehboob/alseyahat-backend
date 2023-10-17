package com.alseyahat.app.feature.wallet.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.wallet.repository.entity.TransactionType;
import com.querydsl.core.types.Predicate;

public interface TransactionTypeService {

	TransactionType save(final TransactionType entity);

	TransactionType findOne(final Predicate predicate);

    Optional<TransactionType> find_One(final Predicate predicate);

    Page<TransactionType> findAll(final Predicate predicate, final Pageable pageable);

}
