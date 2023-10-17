package com.alseyahat.app.feature.customer.controller;

import com.alseyahat.app.feature.customer.dto.CustomerDetailResponse;
import com.alseyahat.app.feature.customer.dto.CustomerFeedBackCreateResponse;
import com.alseyahat.app.feature.customer.dto.CustomerLoginRequest;
import com.alseyahat.app.feature.customer.dto.CustomerRegisterRequest;
import com.alseyahat.app.feature.customer.dto.CustomerUpdateRequest;
import com.alseyahat.app.feature.customer.dto.CustomerUpdateResponse;
import com.alseyahat.app.feature.customer.dto.ForgotPassRequest;
import com.alseyahat.app.feature.customer.dto.ForgotPasswordRequest;
import com.alseyahat.app.feature.customer.facade.CustomerFacade;
import com.alseyahat.app.feature.customer.repository.entity.Customer;
import com.alseyahat.app.feature.employee.dto.ChangePasswordRequest;
import com.alseyahat.app.feature.employee.dto.ChangePasswordResponse;
import com.alseyahat.app.feature.employee.dto.RefreshTokenRequest;
import com.alseyahat.app.feature.employee.dto.ResetPasswordRequest;
import com.alseyahat.app.feature.employee.dto.ResetPasswordResponse;
import com.querydsl.core.types.Predicate;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Api(tags = "Customer")
@RequestMapping("/customers")
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CustomerController {

    CustomerFacade customerFacade;

    @GetMapping("/own-detail")
    @ApiOperation(value = "Get customer own detail", nickname = "getCustomerOwnDetail", notes = "Get customer own detail")
    public ResponseEntity<CustomerDetailResponse> getCustomerDetail() {
    	return new ResponseEntity<>(customerFacade.findCustomerDetail(), HttpStatus.OK);
    }
    
 
    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get customer login token", nickname = "getCustomerToken", notes = "Get customer login token")
    public ResponseEntity<OAuth2AccessToken> getCustomerToken(@Valid @RequestBody final CustomerLoginRequest request) {
        return customerFacade.getCustomerToken(request);
    }

    @PostMapping(value = "/refresh_token", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Refresh Token", nickname = "employeeLogin", notes = "Refresh Token")
    public ResponseEntity<OAuth2AccessToken> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return customerFacade.refreshToken(request.getToken());
    }

    @ApiOperation(value = "Register customer if there is not customer or get token", nickname = "registerCustomer", notes = "Register customer if there is not customer or get token")
    @PostMapping(value = "/register-customer", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OAuth2AccessToken> registerCustomer(@Valid @RequestBody final CustomerRegisterRequest request) {
        return customerFacade.registerCustomer(request);
    }

    @ApiOperation(value = "Update customer user detail", nickname = "updateCustomerDetails", notes = "Update customer user detail")
    @PutMapping(value = "/{customerId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerUpdateResponse> updateCustomerDetails(@PathVariable("customerId") final String customerId,
                                                                        @Valid @RequestBody final CustomerUpdateRequest request) {
        return new ResponseEntity<>(customerFacade.updateCustomer(customerId, request), HttpStatus.OK);
    }
    
    @PostMapping(value = "/change-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Change employee password", nickname = "changePassword", notes = "Change employee password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody final ChangePasswordRequest request) {
       	return ResponseEntity.ok(customerFacade.changePassword(request));
   }
    
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get all customers", nickname = "getAllCustomers", notes = "Get all customers")
    public ResponseEntity<Page<CustomerDetailResponse>> getAllCustomers(@QuerydslPredicate(root = Customer.class) final Predicate predicate, final Pageable pageable) {
        return new ResponseEntity<>(customerFacade.findAllCustomer(predicate, pageable), HttpStatus.OK);
    }
    
  @PostMapping(value = "/forgot-password-email", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Send email to employee for forgot password", nickname = "forgotPasswordEmail", notes = "Send email to customer for forgot password")
  public ResponseEntity<Void> forgotPasswordEmail(@Valid @RequestBody final ForgotPasswordRequest request) {
	  customerFacade.forgotPasswordEmail(request);
      return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @PostMapping(value = "/forgot-password", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Send email to employee for forgot password", nickname = "forgotPassword", notes = "Send email to customer for forgot password")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final ForgotPassRequest request) {
	  customerFacade.forgotPassword(request);
      return new ResponseEntity<>(HttpStatus.OK);
  }
  
  @PostMapping(value = "/feed-back", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "customer feed back", nickname = "feedBack", notes = "get customer feedback")
  public ResponseEntity<CustomerFeedBackCreateResponse> feedBack(@RequestParam final String  feedbackHeadng, @RequestParam final String  customerFeedback) {
	 return ResponseEntity.ok( customerFacade.customerFeedBack(feedbackHeadng,customerFeedback));
  }
  
  @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "Reset customer password", nickname = "resetPassword", notes = "Reset customer password")
  public ResponseEntity<ResetPasswordResponse> resetPassword(@RequestBody final ResetPasswordRequest request) {
     	return ResponseEntity.ok(customerFacade.resetPassword(request));
 }
}


