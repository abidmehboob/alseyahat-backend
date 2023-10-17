package com.alseyahat.app.feature.wallet.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.feature.wallet.dto.TransactionDetailResponse;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface TransactionFacade {

	TransactionUpdateResponse updateTransaction(final TransactionUpdateRequest request);

	Page<TransactionDetailResponse> findAllTransactions(final Predicate predicate, final Pageable pageable);

}
