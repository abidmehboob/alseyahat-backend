package com.alseyahat.app.feature.wallet.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.feature.wallet.repository.entity.Wallet;
import com.querydsl.core.types.Predicate;

public interface WalletService {

	Wallet save(final Wallet entity);

	Wallet findOne(final Predicate predicate);

    Optional<Wallet> find_One(final Predicate predicate);

    Page<Wallet> findAll(final Predicate predicate, final Pageable pageable);

}
