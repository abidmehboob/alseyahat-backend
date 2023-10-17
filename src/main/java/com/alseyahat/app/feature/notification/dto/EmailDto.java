package com.alseyahat.app.feature.notification.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class EmailDto {

    @NotEmpty
    private String from;
    @NotNull
    private String[] to;
    @NotNull
    private String[] cc;
    @NotEmpty
    private String subject;
    @NotEmpty
    private Object content;
}
