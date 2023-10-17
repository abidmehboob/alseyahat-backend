package com.alseyahat.app.feature.wallet.controller;

import javax.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alseyahat.app.feature.wallet.dto.TransactionDetailResponse;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateResponse;
import com.alseyahat.app.feature.wallet.facade.TransactionFacade;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;
import com.querydsl.core.types.Predicate;

import lombok.AccessLevel;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@Api(tags = "Transaction")
@RequestMapping("/transaction")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
	TransactionFacade transactionFacade;
		
	    @PatchMapping
	    @ApiOperation(value = "Update Transaction", nickname = "updateTransaction", notes = "Update Transaction")
	    public ResponseEntity<TransactionUpdateResponse> updateTransaction(@Valid @RequestBody final TransactionUpdateRequest transactionUpdateRequest) {
	        return ResponseEntity.ok(transactionFacade.updateTransaction(transactionUpdateRequest));
	    }
  

	    @GetMapping
	    @ApiOperation(value = "Get all transactions", nickname = "getAllTransactions", notes = "Get all transactions")
	    public ResponseEntity<Page<TransactionDetailResponse>> getAllTransactions(@QuerydslPredicate(root = Transaction.class) Predicate predicate, final Pageable pageable) {
	         return ResponseEntity.ok(transactionFacade.findAllTransactions(predicate, pageable));
	    }


}
