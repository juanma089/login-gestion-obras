package com.auth.login.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {
    private String email;
    private String verificationCode;

    @JsonProperty("newPassword")
    private String newPassword;
}