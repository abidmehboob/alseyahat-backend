package com.alseyahat.app.feature.wallet.service.impl;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.wallet.repository.WalletRepository;
import com.alseyahat.app.feature.wallet.repository.entity.Wallet;
import com.alseyahat.app.feature.wallet.service.WalletService;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WalletServiceImpl implements WalletService {

	WalletRepository walletRepository;
	
	@Override
	public Wallet save(Wallet entity) {
		return walletRepository.save(entity);
	}

	@Override
	public Wallet findOne(Predicate predicate) {
		return walletRepository.findOne(predicate).orElseThrow(() -> new ServiceException(ErrorCodeEnum.ENTITY_NOT_FOUND, "error.wallet_entity_not_found"));
	}

	@Override
	public Optional<Wallet> find_One(Predicate predicate) {
		return walletRepository.findOne(predicate);
	}

	@Override
	public Page<Wallet> findAll(Predicate predicate, Pageable pageable) {
		return walletRepository.findAll(predicate, pageable);
	}

}


