package com.alseyahat.app.feature.wallet.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.feature.wallet.dto.WalletCreateRequest;
import com.alseyahat.app.feature.wallet.dto.WalletCreateResponse;
import com.alseyahat.app.feature.wallet.dto.WalletDetailResponse;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateResponse;
import com.querydsl.core.types.Predicate;

public interface WalletFacade {
	
	void deleteWallet(final String walletId);

	WalletDetailResponse findWalletByUserId(final String userId);

	WalletCreateResponse createWallet(final WalletCreateRequest request);

	WalletUpdateResponse updateWallet(int walletId,final WalletUpdateRequest request);

	Page<WalletDetailResponse> findAllWallet(final Predicate predicate, final Pageable pageable);

}
