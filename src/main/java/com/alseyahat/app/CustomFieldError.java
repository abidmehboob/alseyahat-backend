package com.alseyahat.app;

import lombok.Builder;
import lombok.Getter;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class CustomFieldError {

      private String field;

      private String message;

}
