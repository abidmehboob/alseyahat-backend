package com.alseyahat.app.feature.wallet.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;
import com.querydsl.core.types.Predicate;

public interface TransactionService {

	Transaction save(final Transaction entity);

	Transaction findOne(final Predicate predicate);

    Optional<Transaction> find_One(final Predicate predicate);

    Page<Transaction> findAll(final Predicate predicate, final Pageable pageable);

}
