package com.alseyahat.app.feature.vehicle.hired.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import com.alseyahat.app.feature.notification.dto.EmailDto;
import com.alseyahat.app.feature.notification.service.NotificationService;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
import com.alseyahat.app.feature.review.service.ReviewService;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingCreateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingCreateResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingUpdateRequest;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredBookingUpdateResponse;
import com.alseyahat.app.feature.vehicle.hired.dto.PrivateHiredDetailResponse;
import com.alseyahat.app.feature.vehicle.hired.facade.PrivateHiredBookingFacade;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.PrivateHiredBooking;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHired;
import com.alseyahat.app.feature.vehicle.hired.repository.entity.QPrivateHiredBooking;
import com.alseyahat.app.feature.vehicle.hired.service.PrivateHiredBookingService;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PrivateHiredBookingFacadeImpl implements PrivateHiredBookingFacade {
	PrivateHiredBookingService privateHiredBookingService;
	ModelMapper modelMapper;
	CustomerService customerService;
	EmployeeService employeeService;
	PrivateHiredService privateHiredService;
	WalletService walletService;
	TransactionService transactionService;
	CurrencyService currencyService;
	TransactionTypeService transactionTypeService;
	ReviewService reviewService;
	MessageSource messageSource;
	NotificationService<EmailDto> emailDtoNotificationService;

	@Override
	public void deletePrivateHiredBooking(String privateHiredBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			PrivateHiredBooking privateHiredBooking = privateHiredBookingService
					.findOne(QPrivateHiredBooking.privateHiredBooking.privateHiredBookingId.eq(privateHiredBookingId));
			if (privateHiredBooking.isEnabled())
				privateHiredBooking.setEnabled(Boolean.FALSE);
			else
				privateHiredBooking.setEnabled(Boolean.TRUE);

			privateHiredBookingService.save(privateHiredBooking);
			log.trace("PrivateHiredBooking deleted with id [{}]", privateHiredBookingId);
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public PrivateHiredBookingDetailResponse findPrivateHiredBookingId(String privateHiredBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		PrivateHiredBooking privateHiredBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			privateHiredBooking = privateHiredBookingService
					.findOne(QPrivateHiredBooking.privateHiredBooking.privateHiredBookingId.eq(privateHiredBookingId));
			return buildPrivateHiredBookingDetailResponse(privateHiredBooking);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			privateHiredBooking = privateHiredBookingService
					.findOne(QPrivateHiredBooking.privateHiredBooking.privateHiredBookingId.eq(privateHiredBookingId));
			if (!privateHiredBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				return buildPrivateHiredBookingDetailResponse(privateHiredBooking);
			}
		}

	}

	@Override
	public PrivateHiredBookingCreateResponse createPrivateHiredBooking(PrivateHiredBookingCreateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {
			final PrivateHiredBooking savedPrivateHiredBooking = privateHiredBookingService
					.save(buildPrivateHiredBooking(request));
			return PrivateHiredBookingCreateResponse.builder()
					.privateHiredBookingId(savedPrivateHiredBooking.getPrivateHiredBookingId()).build();
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public PrivateHiredBookingUpdateResponse updatePrivateHiredBooking(PrivateHiredBookingUpdateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		PrivateHiredBooking privateHiredBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			privateHiredBooking = privateHiredBookingService
					.findOne(QPrivateHiredBooking.privateHiredBooking.privateHiredBookingId
							.eq(request.getPrivateHiredBookingId()));
			final PrivateHiredBooking updatedPrivateHiredBooking = privateHiredBookingService
					.save(buildPrivateHiredBooking(privateHiredBooking, request));
			return PrivateHiredBookingUpdateResponse.builder()
					.privateHiredBookingId(updatedPrivateHiredBooking.getPrivateHiredBookingId()).build();
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			privateHiredBooking = privateHiredBookingService
					.findOne(QPrivateHiredBooking.privateHiredBooking.privateHiredBookingId
							.eq(request.getPrivateHiredBookingId()));
			if (!privateHiredBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				final PrivateHiredBooking updatedPrivateHiredBooking = privateHiredBookingService
						.save(buildPrivateHiredBooking(privateHiredBooking, request));
				return PrivateHiredBookingUpdateResponse.builder()
						.privateHiredBookingId(updatedPrivateHiredBooking.getPrivateHiredBookingId()).build();
			}

		}

	}

	@Override
	public Page<PrivateHiredBookingDetailResponse> findAllPrivateHiredBooking(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return privateHiredBookingService.findAll(predicate, pageable)
					.map(this::buildPrivateHiredBookingDetailResponse);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return privateHiredBookingService.findAll(
					ExpressionUtils.allOf(predicate,
							QPrivateHiredBooking.privateHiredBooking.customer.customerId.eq(customer.getCustomerId())),
					pageable).map(this::buildPrivateHiredBookingDetailResponse);
		}
	}

	private PrivateHiredBookingDetailResponse buildPrivateHiredBookingDetailResponse(
			final PrivateHiredBooking privateHiredBooking) {
		PrivateHiredBookingDetailResponse response = new PrivateHiredBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<PrivateHiredBooking, PrivateHiredBookingDetailResponse> typeMap = modelMapper
				.typeMap(PrivateHiredBooking.class, PrivateHiredBookingDetailResponse.class);
		typeMap.map(privateHiredBooking, response);
		response.setPrivateHired(buildPrivateHiredDetailResponse(privateHiredBooking.getPrivateHired()));
		CustomerBasicDetails customerBasicDetails = new CustomerBasicDetails();
//		customerBasicDetails.setCnic(privateHiredBooking.getCustomer().getCnic());
		customerBasicDetails.setCustomerId(privateHiredBooking.getCustomer().getCustomerId());
		customerBasicDetails.setEmail(privateHiredBooking.getCustomer().getEmail());
		customerBasicDetails.setName(privateHiredBooking.getCustomer().getName());
		customerBasicDetails.setPhone(privateHiredBooking.getCustomer().getPhone());
		response.setCustomer(customerBasicDetails);
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

		response.setReviewLst(reviewLst);
//		response.setReviewLst(reviewService.findAll(QReview.review1.privateHired.eq(privateHired), Pageable.unpaged()).getContent());
		return response;
	}

	private PrivateHiredBooking buildPrivateHiredBooking(final PrivateHiredBookingCreateRequest request) {
		PrivateHiredBooking privateHiredBooking = new PrivateHiredBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<PrivateHiredBookingCreateRequest, PrivateHiredBooking> typeMap = modelMapper
				.typeMap(PrivateHiredBookingCreateRequest.class, PrivateHiredBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(PrivateHiredBooking::setPrivateHiredBookingId));
		typeMap.addMappings(mapper -> mapper.skip(PrivateHiredBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(PrivateHiredBooking::setLastUpdated));
		typeMap.map(request, privateHiredBooking);
		PrivateHired privateHired = privateHiredService
				.findOne(QPrivateHired.privateHired.privateHiredId.eq(request.getPrivateHiredId()));
		privateHiredBooking.setPrivateHired(privateHired);
		int totalBookedDays = AppUtils.getDifferenceDays(request.getStartDate(), request.getEndDate());
		String username = AppUtils.getUserNameFromAuthentication();
		Customer customer = customerService
				.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
		privateHiredBooking.setCustomer(customer);
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
		double totalFare = privateHired.getPerDayRate() * totalBookedDays;
		privateHiredBooking.setTotalFare(totalFare);
		if (wallet.isPresent()) {

			BigDecimal remaingBalance = wallet.get().getBalance().subtract(BigDecimal.valueOf(totalFare));
			if (remaingBalance.doubleValue() < 0) {
				privateHiredBooking.setBookingStatus("PENDING");
				sendCreateCustomerNotification(privateHired.getName(), customer.getName(),
						request.getStartDate().toLocaleString(), request.getEndDate().toLocaleString(), customer.getEmail(),
						privateHiredBooking.getBookingStatus());
				return privateHiredBooking;
			} else {
				wallet.get().setBalance(remaingBalance);
				Transaction transaction = new Transaction();
				transaction.setAmount(BigDecimal.valueOf(totalFare));
				transaction.setCurrency(currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
				transaction.setGlobalId("Alseyahat");
				transaction.setDescription("Customer Booked Private Hired");
				transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
				transaction.setWallet(wallet.get());
				transactionService.save(transaction);
			}
		}
		String email = customer.getEmail() ;

		sendCreateCustomerNotification(privateHired.getName(), customer.getName(),
				request.getStartDate().toLocaleString(), request.getEndDate().toLocaleString(), email,
				privateHiredBooking.getBookingStatus());

		return privateHiredBooking;
	}

	private PrivateHiredBooking buildPrivateHiredBooking(PrivateHiredBooking privateHired,
			final PrivateHiredBookingUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<PrivateHiredBookingUpdateRequest, PrivateHiredBooking> typeMap = modelMapper
//				.typeMap(PrivateHiredBookingUpdateRequest.class, PrivateHiredBooking.class);
////		typeMap.addMappings(mapper -> mapper.skip(PrivateHiredBooking::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(PrivateHiredBooking::setLastUpdated));
//		typeMap.map(request, privateHired);
		if (request.getBookingStatus() != null && !request.getBookingStatus().isEmpty())
			privateHired.setBookingStatus(request.getBookingStatus());
		if (request.getStartDate() != null)
			privateHired.setStartDate(request.getStartDate());
		if (request.getEndDate() != null)
			privateHired.setEndDate(request.getEndDate());
		if (request.getPickLocation() != null && !request.getPickLocation().isEmpty())
			privateHired.setPickLocation(request.getPickLocation());
		if (request.getPaymentReceipt() != null && !request.getPaymentReceipt().isEmpty())
			privateHired.setPaymentReceipt(request.getPaymentReceipt());

		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return privateHired;
	}

	@Override
	public Page<PrivateHiredBookingDetailResponse> findAllPrivateHiredBooking(Map<String, Object> parameters,
			Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QPrivateHiredBooking.privateHiredBooking);
		log.trace("Finding private hired bookings predicate [{}]", predicate);
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return privateHiredBookingService.findAll(predicate, pageable)
					.map(this::buildPrivateHiredBookingDetailResponse);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return privateHiredBookingService.findAll(
					ExpressionUtils.allOf(predicate,
							QPrivateHiredBooking.privateHiredBooking.customer.customerId.eq(customer.getCustomerId())),
					pageable).map(this::buildPrivateHiredBookingDetailResponse);
		}
	}

	private void sendCreateCustomerNotification(final String hotelName, final String name, final String fromDate,
			final String toDate, final String email, final String bookingStatus) {
		try {
			final EmailDto emailDto = new EmailDto();
			emailDto.setFrom(messageSource.getMessage("customer.email.private.hired.booking.from", null,
					LocaleContextHolder.getLocale()));
			emailDto.setTo(new String[] { email });
			final Map<String, Object> content = new HashMap<>();
			content.put("name", name);
			content.put("hotelName", hotelName);
			content.put("fromDate", fromDate);
			content.put("toDate", toDate);
			content.put("status", bookingStatus);
			emailDto.setContent(content);
			emailDto.setSubject(messageSource.getMessage("customer.email.private.hired.booking.subject", null,
					LocaleContextHolder.getLocale()));
			emailDtoNotificationService.prepareAndSendMessage(emailDto, "email", Locale.forLanguageTag("en"),
					"/customer-private.hired-booking-email-template.ftl");
			log.trace("Email sent to email [{}]", email);
		} catch (final Exception ex) {
			log.trace("Unable to send email to recipient [{}] due to [{}]", email, ex);
		}
	}

}
