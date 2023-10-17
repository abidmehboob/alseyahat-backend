package com.alseyahat.app.exception;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alseyahat.app.exception.dto.ErrorResponse;
import static com.alseyahat.app.exception.constant.ErrorCodeEnum.INVALID_PARAMETER;
import static java.util.Optional.ofNullable;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true)
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class DefaultExceptionHandler {

    private static final String EXCEPTION_OCCURRED_MSG = "An exception occurred: ";

    private MessageSource messageSource;

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleException(final Throwable ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> handleException(final ServiceException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(getLocalizedMessage(ex.getMessage()), Collections.emptyList()),
                ofNullable(ex.getHttpStatus()).orElse(HttpStatus.NOT_ACCEPTABLE));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleException(final HttpMessageNotReadableException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleException(final MethodArgumentNotValidException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        final List<ErrorResponse.ErrorInfo> errorInfoList = new ArrayList<>();
        final ErrorResponse errorResponse = buildErrorResponse(INVALID_PARAMETER.name(), errorInfoList);
        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            final ErrorResponse.ErrorInfo errorInfo = ErrorResponse.ErrorInfo.builder()
                    .message(getLocalizedMessage(fieldError.getDefaultMessage()))
                    .reason(fieldError.getCode()).domain(fieldError.getField()).build();
            errorInfoList.add(errorInfo);
        });
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleException(final HttpRequestMethodNotSupportedException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleException(final BadCredentialsException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        final Map<String, Object> error = new HashMap<>();
        error.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        error.put("error_description", "Invalid Credentials");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleException(final AccessDeniedException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        final Map<String, Object> error = new HashMap<>();
        error.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
        error.put("error_description", "You don't have permission to access the resource.");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleException(final IllegalArgumentException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Object> handleException(final MissingServletRequestParameterException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        List<ErrorResponse.ErrorInfo> errorsInfo = new ArrayList<>();
        errorsInfo.add(ErrorResponse.ErrorInfo.builder().domain(ex.getParameterName())
                .message(ex.getMessage()).reason(INVALID_PARAMETER.name()).build());
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), errorsInfo), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleException(final ConstraintViolationException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Object> handleException(final HttpMediaTypeNotSupportedException ex) {
        log.error(EXCEPTION_OCCURRED_MSG, ex);
        return new ResponseEntity<>(buildErrorResponse(ExceptionUtils.getRootCauseMessage(ex), Collections.emptyList()), HttpStatus.NOT_ACCEPTABLE);
    }

    private ErrorResponse buildErrorResponse(final String message, final List<ErrorResponse.ErrorInfo> errorsInfo) {
        return ErrorResponse.builder().code(1000).message(message).errors(errorsInfo).build();
    }

    private String getLocalizedMessage(final String message) {
        try {
            return messageSource.getMessage(message, null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ex) {
            log.warn("localized message not found", ex);
        }
        return message;
    }

}
