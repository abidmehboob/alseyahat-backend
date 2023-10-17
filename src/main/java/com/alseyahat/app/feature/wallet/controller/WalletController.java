package com.alseyahat.app.feature.wallet.controller;


import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alseyahat.app.feature.wallet.dto.WalletDetailResponse;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.WalletUpdateResponse;
import com.alseyahat.app.feature.wallet.facade.WalletFacade;
import lombok.AccessLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@Api(tags = "Wallet")
@RequestMapping("/wallet")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WalletController {
	WalletFacade walletFacade;
		
	    @PatchMapping("/{walletId}")
	    @ApiOperation(value = "Update Wallet", nickname = "updateWallet", notes = "Update Wallet")
	    public ResponseEntity<WalletUpdateResponse> updateWallet(@PathVariable final int walletId, @Valid @RequestBody final WalletUpdateRequest walletUpdateRequest) {
	        return ResponseEntity.ok(walletFacade.updateWallet(walletId,walletUpdateRequest));
	    }
  

	    @GetMapping("/{userId}")
	    @ApiOperation(value = "Get Wallet user by id", nickname = "getWalletByUserId", notes = "Get Wallet by user id")
	    public ResponseEntity<WalletDetailResponse> getWalletByUserId(@PathVariable final String userId) {
	        return ResponseEntity.ok(walletFacade.findWalletByUserId(userId));
	    }


}
