package com.alseyahat.app.feature.employee.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotEmpty;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ResetPasswordRequest {


    @NotEmpty
    String phone;
    
    
    String email;

    @NotEmpty
    String newPassword;

    @NotEmpty
    String confirmPassword;


}