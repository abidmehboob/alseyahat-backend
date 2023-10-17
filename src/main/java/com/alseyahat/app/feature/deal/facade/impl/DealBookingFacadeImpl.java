package com.alseyahat.app.feature.deal.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
import com.alseyahat.app.feature.deal.dto.DealBookingCreateRequest;
import com.alseyahat.app.feature.deal.dto.DealBookingCreateResponse;
import com.alseyahat.app.feature.deal.dto.DealBookingDetailResponse;
import com.alseyahat.app.feature.deal.dto.DealBookingUpdateRequest;
import com.alseyahat.app.feature.deal.dto.DealBookingUpdateResponse;
import com.alseyahat.app.feature.deal.dto.DealDetailResponse;
import com.alseyahat.app.feature.deal.facade.DealBookingFacade;
import com.alseyahat.app.feature.deal.repository.entity.Deal;
import com.alseyahat.app.feature.deal.repository.entity.DealBooking;
import com.alseyahat.app.feature.deal.repository.entity.QDeal;
import com.alseyahat.app.feature.deal.repository.entity.QDealBooking;
import com.alseyahat.app.feature.deal.service.DealBookingService;
import com.alseyahat.app.feature.deal.service.DealService;
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.employee.repository.entity.QEmployee;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.hotel.dto.HotelDetailResponse;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.hotel.repository.entity.QHotel;
import com.alseyahat.app.feature.hotel.service.HotelService;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.alseyahat.app.feature.sightSeeing.dto.SightSeeingDetailResponse;
import com.alseyahat.app.feature.sightSeeing.repository.entity.QSightSeeing;
import com.alseyahat.app.feature.sightSeeing.repository.entity.SightSeeing;
import com.alseyahat.app.feature.sightSeeing.service.SightSeeingService;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHired;
import com.alseyahat.app.feature.vehicle.hired.service.PrivateHiredService;
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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DealBookingFacadeImpl implements DealBookingFacade {

	DealBookingService dealBookingService;
	ModelMapper modelMapper;
	DealService dealService;
	CustomerService customerService;
	EmployeeService employeeService;
	HotelService hotelService;
	SightSeeingService sightSeeingService;
	PrivateHiredService privateHiredService;
	ReviewService reviewService;
	WalletService walletService;
	TransactionService transactionService;
	CurrencyService currencyService;
	TransactionTypeService transactionTypeService;

	@Override
	public void deleteDealBooking(String dealBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			DealBooking dealBooking = dealBookingService
					.findOne(QDealBooking.dealBooking.dealBookingId.eq(dealBookingId));
			if (dealBooking.isEnabled())
				dealBooking.setEnabled(Boolean.FALSE);
			else
				dealBooking.setEnabled(Boolean.TRUE);

			dealBookingService.save(dealBooking);
			log.trace("Deal deleted with id [{}]", dealBookingId);
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public DealBookingDetailResponse findDealBookingId(String dealBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		DealBooking dealBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			dealBooking = dealBookingService.findOne(QDealBooking.dealBooking.dealBookingId.eq(dealBookingId));
			return buildDealBookingDetailResponse(dealBooking);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			dealBooking = dealBookingService.findOne(QDealBooking.dealBooking.dealBookingId.eq(dealBookingId));
			if (!dealBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				return buildDealBookingDetailResponse(dealBooking);
			}
		}
	}

	@Override
	public DealBookingCreateResponse createDealBooking(DealBookingCreateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {
			final DealBooking savedDealBooking = dealBookingService.save(buildDealBooking(request));
			return DealBookingCreateResponse.builder().dealBookingId(savedDealBooking.getDealBookingId()).build();
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public DealBookingUpdateResponse updateDealBooking(DealBookingUpdateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		DealBooking dealBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			dealBooking = dealBookingService
					.findOne(QDealBooking.dealBooking.dealBookingId.eq(request.getDealBookingId()));
			final DealBooking updatedDealBooking = dealBookingService.save(buildDealBooking(dealBooking, request));
			return DealBookingUpdateResponse.builder().dealBookingId(updatedDealBooking.getDealBookingId()).build();
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			dealBooking = dealBookingService
					.findOne(QDealBooking.dealBooking.dealBookingId.eq(request.getDealBookingId()));
			if (!dealBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				final DealBooking updatedDealBooking = dealBookingService.save(buildDealBooking(dealBooking, request));
				return DealBookingUpdateResponse.builder().dealBookingId(updatedDealBooking.getDealBookingId()).build();
			}

		}
	}

	@Override
	public Page<DealBookingDetailResponse> findAllDealBookings(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return dealBookingService.findAll(predicate, pageable).map(this::buildDealBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return dealBookingService
					.findAll(
							ExpressionUtils.allOf(predicate,
									QDealBooking.dealBooking.customer.customerId.eq(customer.getCustomerId())),
							pageable)
					.map(this::buildDealBookingDetailResponse);
		}
	}

	private DealBookingDetailResponse buildDealBookingDetailResponse(final DealBooking dealBooking) {
		DealBookingDetailResponse response = new DealBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<DealBooking, DealBookingDetailResponse> typeMap = modelMapper.typeMap(DealBooking.class,
				DealBookingDetailResponse.class);
		typeMap.map(dealBooking, response);
		response.setDealDetailResponse(buildDealDetailResponse(dealBooking.getDeal()));
		CustomerBasicDetails customerBasicDetails = new CustomerBasicDetails();
//		customerBasicDetails.setCnic(dealBooking.getCustomer().getCnic());
		customerBasicDetails.setCustomerId(dealBooking.getCustomer().getCustomerId());
		customerBasicDetails.setEmail(dealBooking.getCustomer().getEmail());
		customerBasicDetails.setName(dealBooking.getCustomer().getName());
		customerBasicDetails.setPhone(dealBooking.getCustomer().getPhone());
		response.setCustomer(customerBasicDetails);
		response.setBookedDate(dealBooking.getBookedDate());
		return response;
	}

	private DealBooking buildDealBooking(final DealBookingCreateRequest request) {
		DealBooking dealBooking = new DealBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<DealBookingCreateRequest, DealBooking> typeMap = modelMapper
				.typeMap(DealBookingCreateRequest.class, DealBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(DealBooking::setDealBookingId));
		typeMap.addMappings(mapper -> mapper.skip(DealBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(DealBooking::setLastUpdated));
		typeMap.map(request, dealBooking);
		Deal deal = dealService.findOne(QDeal.deal.dealId.eq(request.getDealId()));
		String username = AppUtils.getUserNameFromAuthentication();
		Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
		dealBooking.setCustomer(customer);
		dealBooking.setDeal(deal);
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
		double totalFare = deal.getDealAmount();
		if (wallet.isPresent()) {

			BigDecimal remaingBalance=wallet.get().getBalance().subtract(BigDecimal.valueOf(totalFare));
			if (remaingBalance.doubleValue() < 0) {
				dealBooking.setBookingStatus("PENDING");
				return dealBooking;
			}else {
				wallet.get().setBalance(remaingBalance);
				walletService.save(wallet.get());
				Transaction transaction = new Transaction();
				transaction.setAmount(BigDecimal.valueOf(totalFare));
				transaction.setCurrency(currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
				transaction.setGlobalId("Alseyahat");
				transaction.setDescription("Customer Booked Deal");
				transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
				transaction.setWallet(wallet.get());
				transactionService.save(transaction);
			}
		}
		return dealBooking;
	}

	private DealBooking buildDealBooking(DealBooking dealBooking, final DealBookingUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<DealBookingUpdateRequest, DealBooking> typeMap = modelMapper.typeMap(DealBookingUpdateRequest.class,
//				DealBooking.class);
//		typeMap.addMappings(mapper -> mapper.skip(DealBooking::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(DealBooking::setLastUpdated));
//		typeMap.map(request, dealBooking);
		
		if(request.getBookingStatus()!=null &&!request.getBookingStatus().isEmpty())
			dealBooking.setBookingStatus(request.getBookingStatus());
		if(request.getBookedDate()!=null)
			dealBooking.setBookedDate(request.getBookedDate());
		
		return dealBooking;
	}

	private DealDetailResponse buildDealDetailResponse(final Deal deal) {
		DealDetailResponse response = new DealDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Deal, DealDetailResponse> typeMap = modelMapper.typeMap(Deal.class, DealDetailResponse.class);
		typeMap.map(deal, response);
		if (StringUtils.isNotEmpty(deal.getHotelId())) {
			Optional<Hotel> hotel = hotelService.find_One(QHotel.hotel.hotelId.eq(deal.getHotelId()));
			response.setHotelDetailResponse(buildHotelDetailResponse(hotel.get()));
		}

		if (StringUtils.isNotEmpty(deal.getSightSeeingId())) {
			Optional<SightSeeing> sightSeeing = sightSeeingService
					.find_One(QSightSeeing.sightSeeing.sightSeeingId.eq(deal.getSightSeeingId()));
			response.setSightSeeingDetailResponse(buildSightSeeingDetailResponse(sightSeeing.get()));
		}

		if (StringUtils.isNotEmpty(deal.getPrivateHiredId())) {
			Optional<PrivateHired> privateHired = privateHiredService
					.find_One(QPrivateHired.privateHired.privateHiredId.eq(deal.getPrivateHiredId()));
			response.setPrivateHiredDetailResponse(buildPrivateHiredDetailResponse(privateHired.get()));
		}
		return response;
	}

	private HotelDetailResponse buildHotelDetailResponse(final Hotel hotel) {
		HotelDetailResponse response = new HotelDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Hotel, HotelDetailResponse> typeMap = modelMapper.typeMap(Hotel.class, HotelDetailResponse.class);
		typeMap.map(hotel, response);
//		response.setHotelFacilityLst(hotelFacilityService.findAll(QHotelFacility.hotelFacility.hotel.eq(hotel), Pageable.unpaged()).getContent());
		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.hotel.eq(hotel), Pageable.unpaged()).getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
//		response.setReviewLst(reviewService.findAll(QReview.review1.hotel.eq(hotel), Pageable.unpaged()).getContent());
		return response;
	}

	private SightSeeingDetailResponse buildSightSeeingDetailResponse(final SightSeeing sightSeeing) {
		SightSeeingDetailResponse response = new SightSeeingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<SightSeeing, SightSeeingDetailResponse> typeMap = modelMapper.typeMap(SightSeeing.class,
				SightSeeingDetailResponse.class);
		typeMap.map(sightSeeing, response);
		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.sightSeeing.eq(sightSeeing), Pageable.unpaged())
				.getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
		response.setReviewLst(reviewLst);

//		response.setReviewLst(reviewService.findAll(QReview.review1.sightSeeing.eq(sightSeeing), Pageable.unpaged()).getContent());
		return response;
	}

	private PrivateHiredDetailResponse buildPrivateHiredDetailResponse(final PrivateHired privateHired) {
		PrivateHiredDetailResponse response = new PrivateHiredDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<PrivateHired, PrivateHiredDetailResponse> typeMap = modelMapper.typeMap(PrivateHired.class,
				PrivateHiredDetailResponse.class);
		typeMap.map(privateHired, response);
		List<ReviewDto> reviewLst = new ArrayList<ReviewDto>();

		List<Review> reviews = reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged())
				.getContent();
		reviews.forEach(review -> {
			final Review reviewObj = review;
			ReviewDto reviewResponse = new ReviewDto();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<Review, ReviewDto> type = modelMapper.typeMap(Review.class, ReviewDto.class);
			type.map(reviewObj, reviewResponse);
			reviewLst.add(reviewResponse);
		});
//		response.setReviewLst(reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged()).getContent());
		return response;
	}

	@Override
	public Page<DealBookingDetailResponse> findAllDealBookings(Map<String, Object> parameters, Pageable pageable) {

		Predicate predicate = CustomPredicates.toPredicate(parameters, QDealBooking.dealBooking);
		log.trace("Finding private hired bookings predicate [{}]", predicate);
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return dealBookingService.findAll(predicate, pageable).map(this::buildDealBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return dealBookingService
					.findAll(
							ExpressionUtils.allOf(predicate,
									QDealBooking.dealBooking.customer.customerId.eq(customer.getCustomerId())),
							pageable)
					.map(this::buildDealBookingDetailResponse);
		}
	}

}
