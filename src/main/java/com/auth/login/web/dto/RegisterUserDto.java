package com.auth.login.web.dto;

import com.auth.login.model.enums.RoleType;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    @Email
    private String email;
    private String password;
    private String fullName;
    private RoleType role;
    private String numberID;
    private Integer adminID;
}