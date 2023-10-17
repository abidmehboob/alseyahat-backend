package com.alseyahat.app.feature.wallet.facade.impl;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.feature.wallet.dto.WalletCreateRequest;
import com.alseyahat.app.feature.wallet.dto.WalletCreateResponse;
import com.alseyahat.app.feature.wallet.dto.WalletDetailResponse;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateResponse;
import com.alseyahat.app.feature.wallet.facade.WalletFacade;
import com.alseyahat.app.feature.wallet.repository.entity.QWallet;
import com.alseyahat.app.feature.wallet.repository.entity.Wallet;
import com.alseyahat.app.feature.wallet.service.WalletService;
import com.querydsl.core.types.Predicate;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class WalletFacadeImpl implements WalletFacade {
	WalletService walletService;
	ModelMapper modelMapper;
	
	@Override
	public void deleteWallet(String walletId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public WalletDetailResponse findWalletByUserId(String userId) {
		return buildWalletDetailResponse(walletService.findOne(QWallet.wallet.userId.eq(userId)));
	}

	@Override
	public WalletCreateResponse createWallet(WalletCreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WalletUpdateResponse updateWallet(int walletId, WalletUpdateRequest request) {
		Wallet wallet =walletService.findOne(QWallet.wallet.id.eq(walletId));
		Wallet savedWallet = walletService.save(buildWallet(wallet,request));
		return WalletUpdateResponse.builder().walletId(savedWallet.getId()).build();
	}

	@Override
	public Page<WalletDetailResponse> findAllWallet(Predicate predicate, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private WalletDetailResponse buildWalletDetailResponse(final Wallet wallet) {
		WalletDetailResponse response = new WalletDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Wallet, WalletDetailResponse> typeMap = modelMapper.typeMap(Wallet.class, WalletDetailResponse.class);
		typeMap.map(wallet, response);
		return response;
	}
	
	private Wallet buildWallet(Wallet wallet, final WalletUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<WalletUpdateRequest, Wallet> typeMap = modelMapper.typeMap(WalletUpdateRequest.class, Wallet.class);
//		typeMap.addMappings(mapper -> mapper.skip(Wallet::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(Wallet::setLastUpdated));
//		typeMap.map(request, wallet);
		if(request.getBalance()!=null && request.getBalance().intValue()>0)
			wallet.setBalance(request.getBalance());
		if(request.getPaymentReceipt()!=null && !request.getPaymentReceipt().equals(""))
			wallet.setPaymentReceipt(request.getPaymentReceipt());
		
		wallet.setLastUpdated(new Date());
		final String username = AppUtils.getUserNameFromAuthentication();
		wallet.setLastUpdatedBy(username);
		
		return wallet;
	}

}
