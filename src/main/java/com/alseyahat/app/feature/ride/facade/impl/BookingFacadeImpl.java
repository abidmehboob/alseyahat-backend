package com.alseyahat.app.feature.ride.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.LOW_BALANCE;
import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.commons.CustomPredicates;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.driver.repository.entity.DriverBooking;
import com.alseyahat.app.feature.driver.service.DriverBookingService;
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.employee.repository.entity.QEmployee;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.ride.dto.BookingCreateRequest;
import com.alseyahat.app.feature.ride.dto.BookingCreateResponse;
import com.alseyahat.app.feature.ride.dto.BookingDetailResponse;
import com.alseyahat.app.feature.ride.dto.BookingUpdateRequest;
import com.alseyahat.app.feature.ride.dto.BookingUpdateResponse;
import com.alseyahat.app.feature.ride.facade.BookingFacade;
import com.alseyahat.app.feature.ride.repository.entity.QBooking;
import com.alseyahat.app.feature.ride.repository.entity.Booking;
import com.alseyahat.app.feature.ride.service.BookingService;
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
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BookingFacadeImpl implements BookingFacade {

	BookingService bookingService;
	ModelMapper modelMapper;
	CustomerService customerService;
	EmployeeService employeeService;
	CurrencyService currencyService;
	TransactionTypeService transactionTypeService;
	TransactionService transactionService;
	WalletService walletService;
	DriverBookingService driverBookingService;

	@Override
	public void deleteBooking(String bookingId) {
		Booking booking = bookingService.findOne(QBooking.booking.bookingId.eq(bookingId));
		if (booking.isEnabled())
			booking.setEnabled(Boolean.FALSE);
		else
			booking.setEnabled(Boolean.TRUE);

		bookingService.save(booking);
		log.trace("Booking deleted with id [{}]", bookingId);
	}

	@Override
	public BookingDetailResponse findBookingId(String bookingId) {
		Booking booking;
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			booking = bookingService.findOne(QBooking.booking.bookingId.eq(bookingId));
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			booking = bookingService.findOne(QBooking.booking.bookingId.eq(bookingId));
			if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId()))
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
		}
		return buildBookingDetailResponse(booking);
	}

	@Override
	public BookingCreateResponse createBooking(BookingCreateRequest request) {

		final String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {
			final Booking savedBooking = bookingService.save(buildBooking(request));
			return BookingCreateResponse.builder().BookingId(savedBooking.getBookingId()).build();
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public BookingUpdateResponse updateBooking(BookingUpdateRequest request) {

		final String username = AppUtils.getUserNameFromAuthentication();
		Booking booking;
		Booking savedBooking = null;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(employee.get().getEmployeeId()));
		if (employee.isPresent()) {
			booking = bookingService.findOne(QBooking.booking.bookingId.eq(request.getBookingId()));
			Booking customerBooking = buildBooking(booking, request);
			if (employee.get().getRoleId().equalsIgnoreCase("4")) {
				if (request.getRideStatus().equalsIgnoreCase("ACCEPTED")
						|| request.getRideStatus().equalsIgnoreCase("REJECTED")
						|| request.getRideStatus().equalsIgnoreCase("COMPLETED")) {
					// need to add driver table for ride status
					DriverBooking driverBooking = new DriverBooking();
					driverBooking.setBooking(customerBooking);
					driverBooking.setBookingStatus(request.getRideStatus());
					driverBooking.setEmployee(employee.get());
					driverBooking.setTotalRent(booking.getTotalRideFare());
					driverBookingService.save(driverBooking);
					if (request.getRideStatus().equalsIgnoreCase("COMPLETED")) {
						if (wallet.isPresent()) {
							wallet.get().setBalance(
									wallet.get().getBalance().add(BigDecimal.valueOf(booking.getTotalRideFare())));
							if (wallet.get().getBalance().intValue() < 0)
								throw new ServiceException(ErrorCodeEnum.BALANCE_BALANCE, LOW_BALANCE);

							walletService.save(wallet.get());
							Transaction transaction = new Transaction();
							transaction.setAmount(BigDecimal.valueOf(booking.getTotalRideFare()));
							transaction.setCurrency(
									currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
							transaction.setGlobalId("Alseyahat");
							transaction.setDescription("Driver Completed Ride");
							transaction.setType(
									transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
							transaction.setWallet(wallet.get());
							transactionService.save(transaction);
						}
					}
					savedBooking = bookingService.save(customerBooking);
				} else
					throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else
				savedBooking = bookingService.save(customerBooking);
			return BookingUpdateResponse.builder().BookingId(savedBooking.getBookingId()).build();
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.phone.eq(username));
			booking = bookingService.findOne(QBooking.booking.bookingId.eq(request.getBookingId()));
			if (!booking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				savedBooking = bookingService.save(buildBooking(booking, request));
				return BookingUpdateResponse.builder().BookingId(savedBooking.getBookingId()).build();
			}

		}

	}

	@Override
	public Page<BookingDetailResponse> findAllBooking(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			if (employee.get().getRoleId().equalsIgnoreCase("4")) {
				return bookingService.findAll(
						ExpressionUtils.allOf(predicate, QBooking.booking.rideStatus.in("CREATED", "ACCEPTED")),
						pageable).map(this::buildBookingDetailResponse);
			} else
				return bookingService.findAll(predicate, pageable).map(this::buildBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return bookingService
					.findAll(ExpressionUtils.allOf(predicate,
							QBooking.booking.customer.customerId.eq(customer.getCustomerId())), pageable)
					.map(this::buildBookingDetailResponse);
		}
	}

	private BookingDetailResponse buildBookingDetailResponse(final Booking booking) {
		BookingDetailResponse response = new BookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Booking, BookingDetailResponse> typeMap = modelMapper.typeMap(Booking.class,
				BookingDetailResponse.class);
		typeMap.map(booking, response);
		CustomerBasicDetails customerBasicDetails = new CustomerBasicDetails();
//		customerBasicDetails.setCnic(booking.getCustomer().getCnic());
		customerBasicDetails.setCustomerId(booking.getCustomer().getCustomerId());
		customerBasicDetails.setEmail(booking.getCustomer().getEmail());
		customerBasicDetails.setName(booking.getCustomer().getName());
		customerBasicDetails.setPhone(booking.getCustomer().getPhone());
		response.setCustomer(customerBasicDetails);

		return response;
	}

	private Booking buildBooking(final BookingCreateRequest request) {
		Booking booking = new Booking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<BookingCreateRequest, Booking> typeMap = modelMapper.typeMap(BookingCreateRequest.class,
				Booking.class);
		typeMap.addMappings(mapper -> mapper.skip(Booking::setBookingId));
		typeMap.addMappings(mapper -> mapper.skip(Booking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(Booking::setLastUpdated));
		typeMap.map(request, booking);
		String username = AppUtils.getUserNameFromAuthentication();
		Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
		booking.setCustomer(customer);
		booking.setRideStatus("CREATED");
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
		double totalRideFare = 0;
		if (request.getTotalTraval()!=null && !request.getTotalTraval().equals(""))
			totalRideFare = Double.valueOf(request.getTotalTraval()) * 20;

		booking.setTotalRideFare(totalRideFare);
		booking.setFareRate(20);

		if (wallet.isPresent()) {
		
			BigDecimal remaingBalance=wallet.get().getBalance().subtract(BigDecimal.valueOf(totalRideFare));
			if (remaingBalance.doubleValue() < 0) {
				booking.setRideStatus("PENDING");
				return booking;
			}else {
				wallet.get().setBalance(remaingBalance);
				Transaction transaction = new Transaction();
				transaction.setAmount(BigDecimal.valueOf(totalRideFare));
				transaction.setCurrency(currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
				transaction.setGlobalId("Alseyahat");
				transaction.setDescription("Customer Booked Ride");
				transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
				transaction.setWallet(wallet.get());
				transactionService.save(transaction);
			}
		}

		return booking;
	}

	private Booking buildBooking(Booking booking, final BookingUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<BookingUpdateRequest, Booking> typeMap = modelMapper.typeMap(BookingUpdateRequest.class, Booking.class);
//		typeMap.addMappings(mapper -> mapper.skip(Booking::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(Booking::setLastUpdated));
//		typeMap.map(request, booking);
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		if(request.getDropOffAddress()!=null &&!request.getDropOffAddress().isEmpty())
			booking.setDropOffAddress(request.getDropOffAddress());
		if(request.getDropOffLat()>0)
			booking.setDropOffLat(request.getDropOffLat());
		if(request.getDropOffLong()>0)
			booking.setDropOffLong(request.getDropOffLong());
		if(request.getDropOffTime()!=null)
			booking.setDropOffTime(request.getDropOffTime());
		if(request.getPickUpAddress()!=null &&!request.getPickUpAddress().isEmpty())
			booking.setPickUpAddress(request.getPickUpAddress());
		if(request.getRideStatus()!=null &&!request.getRideStatus().isEmpty())
			booking.setRideStatus(request.getRideStatus());
		if(request.getPickUpLat()>0)
			booking.setPickUpLat(request.getPickUpLat());
		if(request.getPickUpLong()>0)
			booking.setPickUpLong(request.getPickUpLong());
		if(request.getPickUpTime()!=null)
			booking.setPickUpTime(request.getPickUpTime());
		
		return booking;
	}

	@Override
	public Page<BookingDetailResponse> findAllBooking(Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QBooking.booking);
		log.trace("Finding  bookings predicate [{}]", predicate);
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return bookingService.findAll(predicate, pageable).map(this::buildBookingDetailResponse);
		} else {
			final Customer customer = customerService.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return bookingService
					.findAll(ExpressionUtils.allOf(predicate,
							QBooking.booking.customer.customerId.eq(customer.getCustomerId())), pageable)
					.map(this::buildBookingDetailResponse);
		}
	}

}
