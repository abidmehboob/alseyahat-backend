package com.alseyahat.app.feature.customer.repository.entity;

import com.alseyahat.app.feature.customer.repository.entity.CustomerBasicDetails;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class CustomerBasicDetails {

	
	 String customerId;
	 
	 String name;

	 String email;
	 
	 String cnic;
	 
	 String phone;
}
