package com.alseyahat.app.feature.wallet.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.employee.repository.entity.QEmployee;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.wallet.dto.TransactionDetailResponse;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateRequest;
import com.alseyahat.app.feature.wallet.dto.TransactionUpdateResponse;
import com.alseyahat.app.feature.wallet.facade.TransactionFacade;
import com.alseyahat.app.feature.wallet.repository.entity.QTransaction;
import com.alseyahat.app.feature.wallet.repository.entity.QTransactionType;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;
import com.alseyahat.app.feature.wallet.service.TransactionService;
import com.alseyahat.app.feature.wallet.service.TransactionTypeService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

import org.springframework.stereotype.Service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TransactionFacadeImpl implements TransactionFacade {

	TransactionService transactionService;
	ModelMapper modelMapper;
	CustomerService customerService;
	EmployeeService employeeService;
	 TransactionTypeService transactionTypeService;
	 
	@Override
	public TransactionUpdateResponse updateTransaction(TransactionUpdateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		Transaction transaction;
		final Optional<Employee> employee = employeeService.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			transaction = transactionService.findOne(QTransaction.transaction.id.eq(request.getId()));
			final Transaction updatedTransaction = transactionService.save(buildTransaction(transaction, request));
			return TransactionUpdateResponse.builder().transactionId(updatedTransaction.getId()).build();
		} else {
			
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			}
	}

	@Override
	public Page<TransactionDetailResponse> findAllTransactions(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
		return transactionService.findAll(predicate, pageable).map(this::buildTransactionDetailResponse);
		}else {
			final Customer customer = customerService.findOne(QCustomer.customer.personalKey.eq(username));
			return transactionService.findAll(ExpressionUtils.allOf(predicate, QTransaction.transaction.wallet.userId.eq(customer.getCustomerId())), pageable).map(this::buildTransactionDetailResponse);
		}
	}
	
	
	private Transaction buildTransaction(Transaction transaction, final TransactionUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<TransactionUpdateRequest, Transaction> typeMap = modelMapper.typeMap(TransactionUpdateRequest.class,
				Transaction.class);
		typeMap.addMappings(mapper -> mapper.skip(Transaction::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Transaction::setLastUpdated));
		typeMap.map(request, transaction);
		if(request.getTransactionType().equals("Canceled"))
			transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("2")));
		else if(request.getTransactionType().equals("Canceled"))
			transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("3")));
		
		return transaction;
	}
	
	private TransactionDetailResponse buildTransactionDetailResponse(final Transaction transaction) {
		TransactionDetailResponse response = new TransactionDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Transaction, TransactionDetailResponse> typeMap = modelMapper.typeMap(Transaction.class,
				TransactionDetailResponse.class);
		typeMap.map(transaction, response);
		return response;
	}

}
