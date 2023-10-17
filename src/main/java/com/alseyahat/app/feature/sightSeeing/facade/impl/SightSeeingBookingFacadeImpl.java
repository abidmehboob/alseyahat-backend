package com.alseyahat.app.feature.sightSeeing.facade.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.employee.repository.entity.QEmployee;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingBookingCreateRequest;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingBookingCreateResponse;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingBookingDetailResponse;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingBookingUpdateRequest;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingBookingUpdateResponse;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingDetailResponse;
import com.alseyahat.app.feature.sightSeeing.facade.SightSeeingBookingFacade;
import com.alseyahat.app.feature.sightSeeing.repository.entity.QSightSeeing;
import com.alseyahat.app.feature.sightSeeing.repository.entity.QSightSeeingBooking;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeing;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeingBooking;
import com.alseyahat.app.feature.sightSeeing.service.SightSeeingBookingService;
import com.alseyahat.app.feature.sightSeeing.service.SightSeeingService;
import com.alseyahat.app.feature.wallet.repository.entity.QCurrency;
import com.alseyahat.app.feature.wallet.repository.entity.QTransactionType;
import com.alseyahat.app.feature.wallet.repository.entity.QWallet;
import com.alseyahat.app.feature.wallet.repository.entity.Transaction;
import com.alseyahat.app.feature.wallet.repository.entity.Wallet;
import com.alseyahat.app.feature.wallet.service.CurrencyService;
import com.alseyahat.app.feature.wallet.service.TransactionService;
import com.alseyahat.app.feature.wallet.service.TransactionTypeService;
import com.alseyahat.app.feature.wallet.service.WalletService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SightSeeingBookingFacadeImpl implements SightSeeingBookingFacade {

	SightSeeingBookingService sightSeeingBookingService;

	ModelMapper modelMapper;
	SightSeeingService sightSeeingService;
	CustomerService customerService;
	EmployeeService employeeService;
	WalletService walletService;
	CurrencyService currencyService;
	TransactionTypeService transactionTypeService;
	TransactionService transactionService;

	@Override
	public void deleteSightSeeingBooking(String sightSeeingBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			SightSeeingBooking sightSeeingBooking = sightSeeingBookingService
					.findOne(QSightSeeingBooking.sightSeeingBooking.sightSeeingBookingId.eq(sightSeeingBookingId));
			if (sightSeeingBooking.isEnabled())
				sightSeeingBooking.setEnabled(Boolean.FALSE);
			else
				sightSeeingBooking.setEnabled(Boolean.TRUE);

			sightSeeingBookingService.save(sightSeeingBooking);
			log.trace("HotelBooking deleted with id [{}]", sightSeeingBookingId);
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public SightSeeingBookingDetailResponse findSightSeeingBookingId(String sightSeeingBookingId) {
		// SightSeeingBooking sightSeeingBooking =
		// sightSeeingBookingService.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(sightSeeingBookingId));
		SightSeeingBooking sightSeeingBooking;

		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			sightSeeingBooking = sightSeeingBookingService
					.findOne(QSightSeeingBooking.sightSeeingBooking.sightSeeingBookingId.eq(sightSeeingBookingId));
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.phone.eq(username));
			sightSeeingBooking = sightSeeingBookingService
					.findOne(QSightSeeingBooking.sightSeeingBooking.sightSeeingBookingId.eq(sightSeeingBookingId));
			if (!sightSeeingBooking.getCustomer().getCustomerId().equals(customer.getCustomerId()))
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
		}

		return buildSightSeeingBookingDetailResponse(sightSeeingBooking);
	}

	@Override
	public SightSeeingBookingCreateResponse createSightSeeingBooking(SightSeeingBookingCreateRequest request) {
		String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {
			final SightSeeingBooking savedSightSeeingBooking = sightSeeingBookingService
					.save(buildSightSeeingBooking(request));
			return SightSeeingBookingCreateResponse.builder()
					.sightSeeingBookingId(savedSightSeeingBooking.getSightSeeingBookingId()).build();
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);

	}

	@Override
	public SightSeeingBookingUpdateResponse updateSightSeeingBooking(final SightSeeingBookingUpdateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		SightSeeingBooking sightSeeingBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			sightSeeingBooking = sightSeeingBookingService.findOne(
					QSightSeeingBooking.sightSeeingBooking.sightSeeingBookingId.eq(request.getSightSeeingBookingId()));
			final SightSeeingBooking savedSightSeeingBooking = sightSeeingBookingService
					.save(buildUpdateSightSeeingBooking(sightSeeingBooking, request));
			return SightSeeingBookingUpdateResponse.builder()
					.sightSeeingBookingId(savedSightSeeingBooking.getSightSeeingBookingId()).build();
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			sightSeeingBooking = sightSeeingBookingService.findOne(
					QSightSeeingBooking.sightSeeingBooking.sightSeeingBookingId.eq(request.getSightSeeingBookingId()));
			if (!sightSeeingBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				final SightSeeingBooking savedSightSeeingBooking = sightSeeingBookingService
						.save(buildUpdateSightSeeingBooking(sightSeeingBooking, request));
				return SightSeeingBookingUpdateResponse.builder()
						.sightSeeingBookingId(savedSightSeeingBooking.getSightSeeingBookingId()).build();
			}

		}
	}

	@Override
	public Page<SightSeeingBookingDetailResponse> findAllSightSeeingBooking(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return sightSeeingBookingService.findAll(predicate, pageable)
					.map(this::buildSightSeeingBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return sightSeeingBookingService.findAll(
					ExpressionUtils.allOf(predicate,
							QSightSeeingBooking.sightSeeingBooking.customer.customerId.eq(customer.getCustomerId())),
					pageable).map(this::buildSightSeeingBookingDetailResponse);
		}

	}

	private SightSeeingBookingDetailResponse buildSightSeeingBookingDetailResponse(
			SightSeeingBooking sightSeeingBooking) {
		SightSeeingBookingDetailResponse response = new SightSeeingBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeingBooking, SightSeeingBookingDetailResponse> typeMap = modelMapper
				.typeMap(SightSeeingBooking.class, SightSeeingBookingDetailResponse.class);
		typeMap.map(sightSeeingBooking, response);
		Optional<SightSeeing> sightSeeing = sightSeeingService.find_One(QSightSeeing.sightSeeing.sightSeeingId.eq(sightSeeingBooking.getSightSeeing().getSightSeeingId()).and(QSightSeeing.sightSeeing.isEnabled.isTrue()));
		if(sightSeeing.isPresent())
		response.setSightSeeing(buildSightSeeingDetailResponse(sightSeeing.get()));
		
		CustomerBasicDetails customerBasicDetails = new CustomerBasicDetails();
//		customerBasicDetails.setCnic(sightSeeingBooking.getCustomer().getCnic());
		customerBasicDetails.setCustomerId(sightSeeingBooking.getCustomer().getCustomerId());
		customerBasicDetails.setEmail(sightSeeingBooking.getCustomer().getEmail());
		customerBasicDetails.setName(sightSeeingBooking.getCustomer().getName());
		customerBasicDetails.setPhone(sightSeeingBooking.getCustomer().getPhone());
		response.setCustomer(customerBasicDetails);
		return response;
	}

	private SightSeeingBooking buildSightSeeingBooking(final SightSeeingBookingCreateRequest request) {
		SightSeeingBooking sightSeeingBooking = new SightSeeingBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<SightSeeingBookingCreateRequest, SightSeeingBooking> typeMap = modelMapper
				.typeMap(SightSeeingBookingCreateRequest.class, SightSeeingBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setSightSeeingBookingId));
		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setLastUpdated));
		typeMap.map(request, sightSeeingBooking);
		SightSeeing sightSeeing = sightSeeingService
				.findOne(QSightSeeing.sightSeeing.sightSeeingId.eq(request.getSightSeeingId()));
		sightSeeingBooking.setSightSeeing(sightSeeing);
		String username = AppUtils.getUserNameFromAuthentication();
		Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
		sightSeeingBooking.setCustomer(customer);
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
		double totalFare = sightSeeing.getTotalFare();
		sightSeeingBooking.setTotalFare(totalFare);
		if (wallet.isPresent()) {

			
			BigDecimal remaingBalance=wallet.get().getBalance().subtract(BigDecimal.valueOf(totalFare));
			if (remaingBalance.doubleValue() <= 0) {
				sightSeeingBooking.setBookingStatus("PENDING");
				return sightSeeingBooking;
			}else {
				wallet.get().setBalance(remaingBalance);
				walletService.save(wallet.get());
				Transaction transaction = new Transaction();
				transaction.setAmount(BigDecimal.valueOf(totalFare));
				transaction.setCurrency(currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
				transaction.setGlobalId("Alseyahat");
				transaction.setDescription("Customer Sight Seeing");
				transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
				transaction.setWallet(wallet.get());
				transactionService.save(transaction);
			}
		}

		return sightSeeingBooking;
	}

	private SightSeeingBooking buildSightSeeingBooking(SightSeeingBooking sightSeeingBooking,
			final SightSeeingBookingUpdateRequest request) {
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeingBookingUpdateRequest, SightSeeingBooking> typeMap = modelMapper
				.typeMap(SightSeeingBookingUpdateRequest.class, SightSeeingBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setLastUpdated));
		typeMap.map(request, sightSeeingBooking);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return sightSeeingBooking;
	}
	
	private SightSeeingBooking buildUpdateSightSeeingBooking(SightSeeingBooking sightSeeingBooking,
			final SightSeeingBookingUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<SightSeeingBookingUpdateRequest, SightSeeingBooking> typeMap = modelMapper
//				.typeMap(SightSeeingBookingUpdateRequest.class, SightSeeingBooking.class);
//		typeMap.addMappings(mapper -> mapper.skip(SightSeeingBooking::setLastUpdated));
//		typeMap.map(request, sightSeeingBooking);
		if(request.getBookingStatus()!=null && !request.getBookingStatus().isEmpty())
			sightSeeingBooking.setBookingStatus(request.getBookingStatus());
		if(request.getBookedDate()!=null)
			sightSeeingBooking.setBookedDate(request.getBookedDate());
		if(request.getPickLocation()!=null &&!request.getPickLocation().isEmpty())
			sightSeeingBooking.setPickLocation(request.getPickLocation());
		if(request.getPickupLatitude()!=null)
			sightSeeingBooking.setPickupLatitude(request.getPickupLatitude());
		if(request.getPickupLongitude()!=null)
			sightSeeingBooking.setPickupLongitude(request.getPickupLongitude());
		if(request.getPickUpTime()!=null)
			sightSeeingBooking.setPickUpTime(request.getPickUpTime());
		
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return sightSeeingBooking;
	}

	@Override
	public Page<SightSeeingBookingDetailResponse> findAllSightSeeingBooking(Map<String, Object> parameters,
			Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QSightSeeingBooking.sightSeeingBooking);
		log.trace("Finding sight seeing bookings predicate [{}]", predicate);
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return sightSeeingBookingService.findAll(predicate, pageable)
					.map(this::buildSightSeeingBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return sightSeeingBookingService.findAll(
					ExpressionUtils.allOf(predicate,
							QSightSeeingBooking.sightSeeingBooking.customer.customerId.eq(customer.getCustomerId())),
					pageable).map(this::buildSightSeeingBookingDetailResponse);
		}

	}
	
	private SightSeeingDetailResponse buildSightSeeingDetailResponse(final SightSeeing sightSeeing) {
		SightSeeingDetailResponse response = new SightSeeingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeing, SightSeeingDetailResponse> typeMap = modelMapper.typeMap(SightSeeing.class, SightSeeingDetailResponse.class);
		typeMap.map(sightSeeing, response);
		return response;
	}
}
