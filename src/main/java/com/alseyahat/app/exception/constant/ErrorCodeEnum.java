package com.alseyahat.app.exception.constant;

import org.springframework.http.HttpStatus;

import com.alseyahat.app.exception.ErrorPrinter;

public enum ErrorCodeEnum implements ErrorPrinter {

    INVALID_PARAMETER(HttpStatus.NOT_ACCEPTABLE),
    INVALID_CATEGORY(HttpStatus.NOT_ACCEPTABLE),
    ENTITY_NOT_FOUND(HttpStatus.NOT_ACCEPTABLE),
    X_PAY_FAILED(HttpStatus.NOT_ACCEPTABLE),
    INVALID_REQUEST(HttpStatus.NOT_ACCEPTABLE),
    FILE_UPLOAD_FAILED(HttpStatus.NOT_ACCEPTABLE),
    STORE_ALREADY_EXIST(HttpStatus.NOT_ACCEPTABLE),
	CUSTOMER_ALREADY_EXIST(HttpStatus.NOT_ACCEPTABLE),
	BALANCE_BALANCE(HttpStatus.PRECONDITION_FAILED),
	OTP_EXPIRED(HttpStatus.FAILED_DEPENDENCY),
	OTP_NOT_FOUND(HttpStatus.NOT_FOUND);
	

    private final HttpStatus httpStatus;

    ErrorCodeEnum(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}