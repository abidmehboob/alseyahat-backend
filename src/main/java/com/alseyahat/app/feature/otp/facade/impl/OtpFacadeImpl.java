package com.alseyahat.app.feature.otp.facade.impl;

import static com.alseyahat.app.constant.RoleConstant.PERMISSION_DENIED;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import org.apache.commons.lang3.time.DateUtils;

import com.alseyahat.app.commons.AppUtils;
import com.alseyahat.app.commons.OTPHelper;
import com.alseyahat.app.commons.SmsNotification;
import com.alseyahat.app.exception.ServiceException;
import com.alseyahat.app.exception.constant.ErrorCodeEnum;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.customer.repository.entity.QCustomer;
import com.alseyahat.app.feature.customer.service.CustomerService;
import com.alseyahat.app.feature.otp.dto.OtpCreateRequest;
import com.alseyahat.app.feature.otp.dto.OtpCreateRespnse;
import com.alseyahat.app.feature.otp.dto.OtpValidateRequest;
import com.alseyahat.app.feature.otp.dto.OtpValidateRespnse;
import com.alseyahat.app.feature.otp.facade.OtpFacade;
import com.alseyahat.app.feature.otp.repository.entity.QTwoFactorAuth;
import com.alseyahat.app.feature.otp.repository.entity.TwoFactorAuth;
import com.alseyahat.app.feature.otp.service.OtpService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class OtpFacadeImpl implements OtpFacade {

	OtpService otpService;

	OTPHelper otpHelper;

	SmsNotification smsNotification;

	CustomerService customerService;

	@Override
	@SneakyThrows
	public OtpValidateRespnse validateOTP(OtpValidateRequest otpValidateRequest) {
		Optional<TwoFactorAuth> twoFactorAuth = null;
//		final String username = AppUtils.getUserNameFromAuthentication();
		Optional<Customer> customer = customerService.find_One(QCustomer.customer.email
				.eq(otpValidateRequest.getEmail()).or(QCustomer.customer.phone.eq(otpValidateRequest.getNumber())));
		OtpValidateRespnse otpValidateRespnse = new OtpValidateRespnse();
		if (customer.isPresent()) {
			twoFactorAuth = otpService.find_One(QTwoFactorAuth.twoFactorAuth.userId.eq(customer.get().getCustomerId())
					.and(QTwoFactorAuth.twoFactorAuth.phoneNumber.eq(otpValidateRequest.getNumber()))
					.and(QTwoFactorAuth.twoFactorAuth.otpData.eq(otpValidateRequest.getOtpData())));
			if (!twoFactorAuth.isPresent()) {
				twoFactorAuth = otpService.find_One(QTwoFactorAuth.twoFactorAuth.phoneNumber
						.eq("+92"
								+ otpValidateRequest.getNumber().substring(1, otpValidateRequest.getNumber().length()))
						.and(QTwoFactorAuth.twoFactorAuth.otpData.eq(otpValidateRequest.getOtpData())));
			}
		} else {
			twoFactorAuth = otpService.find_One(QTwoFactorAuth.twoFactorAuth.phoneNumber
					.eq("+92" + otpValidateRequest.getNumber().substring(1, otpValidateRequest.getNumber().length()))
					.and(QTwoFactorAuth.twoFactorAuth.otpData.eq(otpValidateRequest.getOtpData())));
		}

		if (!twoFactorAuth.isPresent()) {
			otpValidateRespnse.setValidate(false);
			 throw new ServiceException(ErrorCodeEnum.OTP_NOT_FOUND, "Invalid OTP");
		}
		else {
			SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
			Date time = format.parse(twoFactorAuth.get().getExpiresIn());
			Date currentTime = format.parse(new Date().toString());
			long diff = currentTime.getTime() - time.getTime();
			long diffMinutes = diff / (60 * 1000) % 60;
			if (!twoFactorAuth.get().getStatus().equalsIgnoreCase("I")) {
				otpValidateRespnse.setValidate(false);
				throw new ServiceException(ErrorCodeEnum.OTP_EXPIRED, "OTP Expired");
			} else if (diffMinutes > 3 || !twoFactorAuth.get().getOtpData().equals(otpValidateRequest.getOtpData())) {
				otpValidateRespnse.setValidate(false);
				 throw new ServiceException(ErrorCodeEnum.OTP_EXPIRED, "OTP Expired");
			} else {
				twoFactorAuth.get().setStatus("C");
				twoFactorAuth.get().setConsumedDate(new Date().toString());
				otpService.save(twoFactorAuth.get());
				otpValidateRespnse.setValidate(true);
			}
		}
		//otpValidateRespnse.setValidate(true);
		return otpValidateRespnse;
	}

	@Override
	public OtpCreateRespnse sendOTP(OtpCreateRequest otpCreateRequest) {

//		final String username = AppUtils.getUserNameFromAuthentication();
		Optional<Customer> customer = customerService
				.find_One(QCustomer.customer.phone.eq(otpCreateRequest.getNumber()));

		OtpCreateRespnse otpCreateRespnse = new OtpCreateRespnse();
		if (customer.isPresent()) {
			String otp = otpHelper.generateOTP();
			try {
				Date targetTime = new Date(); // now
//		targetTime = DateUtils.addMinutes(targetTime, 2); // add minute
				TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
				twoFactorAuth.setCreatedDate(new Date().toString());
				// twoFactorAuth.setConsumedDate(new Date().toString());
				twoFactorAuth.setEventTitle(otpCreateRequest.getEventTitle());
				targetTime = DateUtils.addMinutes(targetTime, 5); // add minute
				twoFactorAuth.setExpiresIn(targetTime.toString());
				twoFactorAuth.setPhoneNumber(
						"+92" + otpCreateRequest.getNumber().substring(1, otpCreateRequest.getNumber().length()));
				twoFactorAuth.setOtpData(otp);

				twoFactorAuth.setStatus("I");
				twoFactorAuth.setUserId(customer.get().getCustomerId());
				otpService.save(twoFactorAuth);
				smsNotification.sendSms(twoFactorAuth.getPhoneNumber(), otp);
			} catch (Exception ex) {
				 log.trace("sendOTP couldn't send [{}]", ex.getMessage());
				otpCreateRespnse.setSent(false);
			}

			otpCreateRespnse.setSent(true);
		} else {
			String otp = otpHelper.generateOTP();
			try {
				Date targetTime = new Date(); // now
//		targetTime = DateUtils.addMinutes(targetTime, 2); // add minute
				TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
				twoFactorAuth.setCreatedDate(new Date().toString());
				// twoFactorAuth.setConsumedDate(new Date().toString());
				twoFactorAuth.setEventTitle(otpCreateRequest.getEventTitle());
				targetTime = DateUtils.addMinutes(targetTime, 5); // add minute
				twoFactorAuth.setExpiresIn(targetTime.toString());
				twoFactorAuth.setPhoneNumber(
						"+92" + otpCreateRequest.getNumber().substring(1, otpCreateRequest.getNumber().length()));
				twoFactorAuth.setOtpData(otp);

				twoFactorAuth.setStatus("I");
				otpService.save(twoFactorAuth);
				smsNotification.sendSms(twoFactorAuth.getPhoneNumber(), otp);
			} catch (Exception ex) {
				 log.trace("sendOTP couldn't send [{}]", ex.getMessage());
					otpCreateRespnse.setSent(false);
			}
		}

		return otpCreateRespnse;
	}

}
