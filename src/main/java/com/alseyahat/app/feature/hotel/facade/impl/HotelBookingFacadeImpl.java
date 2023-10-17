package com.alseyahat.app.feature.hotel.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
import com.alseyahat.app.feature.employee.repository.entity.Employee;
import com.alseyahat.app.feature.employee.repository.entity.QEmployee;
import com.alseyahat.app.feature.employee.service.EmployeeService;
import com.alseyahat.app.feature.hotel.dto.HotelBookingCreateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelBookingCreateResponse;
import com.alseyahat.app.feature.hotel.dto.HotelBookingDetailResponse;
import com.alseyahat.app.feature.hotel.dto.HotelBookingUpdateRequest;
import com.alseyahat.app.feature.hotel.dto.HotelBookingUpdateResponse;
import com.alseyahat.app.feature.hotel.dto.HotelDetailResponse;
import com.alseyahat.app.feature.hotel.dto.RoomBookingCreateRequest;
import com.alseyahat.app.feature.hotel.dto.RoomBookingDetailResponse;
import com.alseyahat.app.feature.hotel.facade.HotelBookingFacade;
import com.alseyahat.app.feature.hotel.repository.entity.Hotel;
import com.alseyahat.app.feature.hotel.repository.entity.HotelBooking;
import com.alseyahat.app.feature.hotel.repository.entity.QHotel;
import com.alseyahat.app.feature.hotel.repository.entity.QHotelBooking;
import com.alseyahat.app.feature.hotel.repository.entity.QRoomBooking;
import com.alseyahat.app.feature.hotel.repository.entity.QRoomType;
import com.alseyahat.app.feature.hotel.repository.entity.RoomBooking;
import com.alseyahat.app.feature.hotel.repository.entity.RoomType;
import com.alseyahat.app.feature.hotel.service.HotelBookingService;
import com.alseyahat.app.feature.hotel.service.HotelService;
import com.alseyahat.app.feature.hotel.service.RoomBookingService;
import com.alseyahat.app.feature.hotel.service.RoomTypeService;
import com.alseyahat.app.feature.notification.dto.EmailDto;
import com.alseyahat.app.feature.notification.service.NotificationService;
import com.alseyahat.app.feature.review.dto.ReviewDto;
import com.alseyahat.app.feature.review.repository.entity.QReview;
import com.alseyahat.app.feature.review.repository.entity.Review;
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
public class HotelBookingFacadeImpl implements HotelBookingFacade {

	HotelBookingService hotelBookingService;
	MessageSource messageSource;
	ModelMapper modelMapper;
	HotelService hotelService;
	CustomerService customerService;
	EmployeeService employeeService;
	WalletService walletService;
	RoomTypeService roomTypeService;
	CurrencyService currencyService;
	TransactionTypeService transactionTypeService;
	TransactionService transactionService;
	NotificationService<EmailDto> emailDtoNotificationService;
	RoomBookingService roomBookingService;

	@Override
	public void deleteHotelBooking(String hotelBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			HotelBooking hotelBooking = hotelBookingService
					.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(hotelBookingId));
			if (hotelBooking.isEnabled())
				hotelBooking.setEnabled(Boolean.FALSE);
			else
				hotelBooking.setEnabled(Boolean.TRUE);

			hotelBookingService.save(hotelBooking);
			log.trace("HotelBooking deleted with id [{}]", hotelBookingId);
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public HotelBookingDetailResponse findHotelBookingId(String hotelBookingId) {
		final String username = AppUtils.getUserNameFromAuthentication();
		HotelBooking hotelBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			hotelBooking = hotelBookingService.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(hotelBookingId));
			return buildHotelBookingDetailResponse(hotelBooking);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			hotelBooking = hotelBookingService.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(hotelBookingId));
			if (!hotelBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				return buildHotelBookingDetailResponse(hotelBooking);
			}
		}
	}

	@Override
	public HotelBookingCreateResponse createHotelBooking(HotelBookingCreateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		if (customerService.exist(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)))) {
			final HotelBooking savedHotelBooking = hotelBookingService.save(buildHotelBooking(request));
			if (request.getRooms() != null) {
				request.getRooms().stream().forEach(room -> {
					RoomBooking roomBooking = buildRoomBooking(room);
					roomBooking.setHotelBookingId(savedHotelBooking.getHotelBookingId());
					RoomBooking savedRoomBooking = roomBookingService.save(roomBooking);
					log.trace("HotelBooking Id with id [{}]", savedHotelBooking.getHotelBookingId());
					log.trace("RoomBooking Id with id [{}]", savedRoomBooking.getRoomBookingId());

				});
			}
			return HotelBookingCreateResponse.builder().hotelBookingId(savedHotelBooking.getHotelBookingId()).build();
		} else
			throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
	}

	@Override
	public HotelBookingUpdateResponse updateHotelBooking(HotelBookingUpdateRequest request) {
		final String username = AppUtils.getUserNameFromAuthentication();
		HotelBooking hotelBooking;
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			hotelBooking = hotelBookingService
					.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(request.getHotelBookingId()));
			final HotelBooking savedHotelBooking = hotelBookingService.save(buildHotelBooking(hotelBooking, request));
			return HotelBookingUpdateResponse.builder().hotelBookingId(savedHotelBooking.getHotelBookingId()).build();
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			hotelBooking = hotelBookingService
					.findOne(QHotelBooking.hotelBooking.hotelBookingId.eq(request.getHotelBookingId()));
			if (!hotelBooking.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
				throw new ServiceException(ErrorCodeEnum.INVALID_REQUEST, PERMISSION_DENIED);
			} else {
				final HotelBooking savedHotelBooking = hotelBookingService
						.save(buildHotelBooking(hotelBooking, request));
				return HotelBookingUpdateResponse.builder().hotelBookingId(savedHotelBooking.getHotelBookingId())
						.build();
			}

		}
	}

	@Override
	public Page<HotelBookingDetailResponse> findAllHotelBooking(Predicate predicate, Pageable pageable) {
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return hotelBookingService.findAll(predicate, pageable).map(this::buildHotelBookingDetailResponse);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));

			return hotelBookingService
					.findAll(
							ExpressionUtils.allOf(predicate,
									QHotelBooking.hotelBooking.customer.customerId.eq(customer.getCustomerId())),
							pageable)
					.map(this::buildHotelBookingDetailResponse);
		}
	}

	private HotelBookingDetailResponse buildHotelBookingDetailResponse(final HotelBooking hotelBooking) {
		HotelBookingDetailResponse response = new HotelBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<HotelBooking, HotelBookingDetailResponse> typeMap = modelMapper.typeMap(HotelBooking.class,
				HotelBookingDetailResponse.class);
		typeMap.map(hotelBooking, response);
		response.setHotel(buildHotelDetailResponse(hotelBooking.getHotel()));
		CustomerBasicDetails customerBasicDetails = new CustomerBasicDetails();
//		customerBasicDetails.setCnic(hotelBooking.getCustomer().getCnic());
		customerBasicDetails.setCustomerId(hotelBooking.getCustomer().getCustomerId());
		customerBasicDetails.setEmail(hotelBooking.getCustomer().getEmail());
		customerBasicDetails.setName(hotelBooking.getCustomer().getName());
		customerBasicDetails.setPhone(hotelBooking.getCustomer().getPhone());
		response.setCustomer(customerBasicDetails);
		List<RoomBooking> rooms = roomBookingService
				.findAll(QRoomBooking.roomBooking.hotelBookingId.eq(hotelBooking.getHotelBookingId()),
						Pageable.unpaged())
				.getContent();
		List<RoomBookingDetailResponse> roomLst = new ArrayList<RoomBookingDetailResponse>();
		rooms.forEach(room -> {
			final RoomBooking roomObj = room;
			RoomType roomType = roomTypeService.findOne(QRoomType.roomType.roomTypeId.eq(room.getRoomType()));
			RoomBookingDetailResponse roomResponse = new RoomBookingDetailResponse();
			modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
			TypeMap<RoomBooking, RoomBookingDetailResponse> type = modelMapper.typeMap(RoomBooking.class,
					RoomBookingDetailResponse.class);
			type.map(roomObj, roomResponse);
			roomResponse.setRoomType(roomType.getType());
			roomLst.add(roomResponse);
		});
		response.setRooms(roomLst);
		return response;
	}

	@Transactional
	private HotelBooking buildHotelBooking(final HotelBookingCreateRequest request) {
		HotelBooking hotelBooking = new HotelBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<HotelBookingCreateRequest, HotelBooking> typeMap = modelMapper
				.typeMap(HotelBookingCreateRequest.class, HotelBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(HotelBooking::setHotelBookingId));
		typeMap.addMappings(mapper -> mapper.skip(HotelBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(HotelBooking::setLastUpdated));
		typeMap.map(request, hotelBooking);
		hotelBooking.setHotel(hotelService.findOne(QHotel.hotel.hotelId.eq(request.getHotelId())));
		String username = AppUtils.getUserNameFromAuthentication();

		Customer customer = customerService
				.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
		hotelBooking.setCustomer(customer);
		Optional<Wallet> wallet = walletService.find_One(QWallet.wallet.userId.eq(customer.getCustomerId()));
		int totalBookedDays = AppUtils.getDifferenceDays(request.getStartDate(), request.getEndDate());
		if (wallet.isPresent()) {
			double totalCharges = 0;

			if (request.getRooms() == null) {
				RoomType roomType = roomTypeService.findOne(QRoomType.roomType.roomTypeId.eq(request.getRoomType()));
				double roomCharges = Double.valueOf(roomType.getCharges());

//			double totalCharges =0;
				if (request.getRoomCount() != null && request.getRoomCount() > 0)
					totalCharges = totalBookedDays * (roomCharges * request.getRoomCount());
				else
					totalCharges = totalBookedDays * roomCharges;

				hotelBooking.setTotalRent(totalCharges);
			} else {
				request.getRooms().stream().forEach(room -> {

					RoomType roomType = roomTypeService.findOne(QRoomType.roomType.roomTypeId.eq(room.getRoomType()));
					double roomCharges = Double.valueOf(roomType.getCharges());
					if (room.getRoomCount() != null && room.getRoomCount() > 0) {
						if (hotelBooking.getTotalRent() > 0) {
							hotelBooking.setTotalRent((totalBookedDays * (roomCharges * room.getRoomCount()))
									+ hotelBooking.getTotalRent());
						} else
							hotelBooking.setTotalRent(totalBookedDays * (roomCharges * room.getRoomCount()));
					} else {
						if (hotelBooking.getTotalRent() > 0) {
							hotelBooking.setTotalRent((totalBookedDays * roomCharges) + hotelBooking.getTotalRent());
						} else
							hotelBooking.setTotalRent(totalBookedDays * roomCharges);
					}
				});
				hotelBooking.setAdult(0);
				hotelBooking.setChildern(0);
				hotelBooking.setExtraMatress(0);
				hotelBooking.setRoomCount(0);
				hotelBooking.setRoomType("");
				totalCharges = hotelBooking.getTotalRent();
			}

			BigDecimal remaingBalance = wallet.get().getBalance().subtract(BigDecimal.valueOf(totalCharges));

			if (remaingBalance.doubleValue() < 0) {
				hotelBooking.setBookingStatus("PENDING");
				sendCreateCustomerNotification(hotelBooking.getHotel().getName(), customer.getName(),
						request.getStartDate().toLocaleString(), request.getEndDate().toLocaleString(),
						customer.getEmail(), hotelBooking.getHotel().getEmail(), hotelBooking.getBookingStatus());

				return hotelBooking;
			} else {
				wallet.get().setBalance(remaingBalance);
				Transaction transaction = new Transaction();
				transaction.setAmount(BigDecimal.valueOf(totalCharges));
				transaction.setCurrency(currencyService.findOne(QCurrency.currency.name.equalsIgnoreCase("PKR")));
				transaction.setGlobalId("Alseyahat");
				transaction.setDescription("Customer Booked Hotel");
				transaction.setType(transactionTypeService.findOne(QTransactionType.transactionType.id.eq("1")));
				transaction.setWallet(wallet.get());
				transactionService.save(transaction);
			}
		}

		sendCreateCustomerNotification(hotelBooking.getHotel().getName(), customer.getName(),
				request.getStartDate().toLocaleString(), request.getEndDate().toLocaleString(), customer.getEmail(),
				hotelBooking.getHotel().getEmail(), hotelBooking.getBookingStatus());

		return hotelBooking;
	}

	private HotelBooking buildHotelBooking(HotelBooking hotel, final HotelBookingUpdateRequest request) {
//		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
//		TypeMap<HotelBookingUpdateRequest, HotelBooking> typeMap = modelMapper.typeMap(HotelBookingUpdateRequest.class,
//				HotelBooking.class);
//		typeMap.addMappings(mapper -> mapper.skip(HotelBooking::setDateCreated));
//		typeMap.addMappings(mapper -> mapper.skip(HotelBooking::setLastUpdated));
//		typeMap.map(request, hotel);
		if (request.getBookingStatus() != null && !request.getBookingStatus().isEmpty())
			hotel.setBookingStatus(request.getBookingStatus());
		if (request.getRoomType() != null && !request.getRoomType().isEmpty())
			hotel.setRoomType(request.getRoomType());
		if (request.getRoomCount() != null && request.getRoomCount() > 0)
			hotel.setRoomCount(request.getRoomCount());
		if (request.getPickLocation() != null && !request.getPickLocation().isEmpty())
			hotel.setPickLocation(request.getPickLocation());
		if (request.getStartDate() != null)
			hotel.setStartDate(request.getStartDate());
		if (request.getEndDate() != null)
			hotel.setEndDate(request.getEndDate());
		if (request.getPickLocation() != null && !request.getPickLocation().isEmpty())
			hotel.setPickLocation(request.getPickLocation());
		// category.setImages(request.getImages().stream().map(Object::toString).collect(Collectors.joining(SEPARATOR)));

		return hotel;
	}

	private HotelDetailResponse buildHotelDetailResponse(final Hotel hotel) {
		HotelDetailResponse response = new HotelDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<Hotel, HotelDetailResponse> typeMap = modelMapper.typeMap(Hotel.class, HotelDetailResponse.class);
		typeMap.map(hotel, response);
		return response;
	}

	@Override
	public Page<HotelBookingDetailResponse> findAllHotelBooking(Map<String, Object> parameters, Pageable pageable) {
		Predicate predicate = CustomPredicates.toPredicate(parameters, QHotelBooking.hotelBooking);
		log.trace("Finding hotels predicate [{}]", predicate);
		final String username = AppUtils.getUserNameFromAuthentication();
		final Optional<Employee> employee = employeeService
				.find_One(QEmployee.employee.email.eq(username).or(QEmployee.employee.phone.eq(username)));
		if (employee.isPresent()) {
			return hotelBookingService.findAll(predicate, pageable).map(this::buildHotelBookingDetailResponse);
		} else {
			final Customer customer = customerService
					.findOne(QCustomer.customer.email.eq(username).or(QCustomer.customer.phone.eq(username)));
			return hotelBookingService
					.findAll(
							ExpressionUtils.allOf(predicate,
									QHotelBooking.hotelBooking.customer.customerId.eq(customer.getCustomerId())),
							pageable)
					.map(this::buildHotelBookingDetailResponse);
		}
	}

	private void sendCreateCustomerNotification(final String hotelName, final String name, final String fromDate,
			final String toDate, final String email, final String ccEmail, final String bookingStatus) {
		try {
			final EmailDto emailDto = new EmailDto();
			emailDto.setFrom(messageSource.getMessage("customer.email.hotel.booking.from", null,
					LocaleContextHolder.getLocale()));
			emailDto.setTo(new String[] { email });
			emailDto.setCc(new String[] { ccEmail });
			final Map<String, Object> content = new HashMap<>();
			content.put("name", name);
			content.put("hotelName", hotelName);
			content.put("fromDate", fromDate);
			content.put("toDate", toDate);
			content.put("status", bookingStatus);
			emailDto.setContent(content);
			emailDto.setSubject(messageSource.getMessage("customer.email.hotel.booking.subject", null,
					LocaleContextHolder.getLocale()));
			emailDtoNotificationService.prepareAndSendMessage(emailDto, "email", Locale.forLanguageTag("en"),
					"/customer-hotel-booking-email-template.ftl");
			log.trace("Email sent to email [{}]", email);
		} catch (final Exception ex) {
			System.out.println("Unable to send email to recipient [{}] due to [{}]" + email + ex);
		}
	}

	private RoomBooking buildRoomBooking(final RoomBookingCreateRequest request) {
		RoomBooking roomBooking = new RoomBooking();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		final TypeMap<RoomBookingCreateRequest, RoomBooking> typeMap = modelMapper
				.typeMap(RoomBookingCreateRequest.class, RoomBooking.class);
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setRoomBookingId));
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setDateCreated));
		typeMap.addMappings(mapper -> mapper.skip(RoomBooking::setLastUpdated));
		typeMap.map(request, roomBooking);

		return roomBooking;
	}

	private RoomBookingDetailResponse buildRoomBookingDetailResponse(final RoomBooking roomBooking) {
		RoomBookingDetailResponse response = new RoomBookingDetailResponse();
		modelMapper.getConfiguration().setAmbiguityIgnored(Boolean.TRUE);
		TypeMap<RoomBooking, RoomBookingDetailResponse> typeMap = modelMapper.typeMap(RoomBooking.class,
				RoomBookingDetailResponse.class);
		typeMap.map(roomBooking, response);
		return response;
	}

}
