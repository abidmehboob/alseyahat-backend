package com.alseyahat.app.feature.otp.facade;

import com.alseyahat.app.feature.otp.dto.OtpCreateRequest;
import com.alseyahat.app.feature.otp.dto.OtpCreateRespnse;
import com.alseyahat.app.feature.otp.dto.OtpValidateRequest;
import com.alseyahat.app.feature.otp.dto.OtpValidateRespnse;

public interface OtpFacade {

	OtpValidateRespnse validateOTP(OtpValidateRequest otpValidateRequest);

	OtpCreateRespnse sendOTP(OtpCreateRequest otpCreateRequest);
}