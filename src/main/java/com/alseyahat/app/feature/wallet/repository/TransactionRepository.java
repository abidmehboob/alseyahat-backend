package com.alseyahat.app.feature.wallet.repository;

import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;

public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long>, QuerydslPredicateExecutor<Transaction> {

}
