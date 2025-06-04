package com.auth.login.web.dto;

import com.auth.login.model.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class UserResponseDto {
    private Integer id;
    private String numberID;
    private String fullName;
    private String email;
    private RoleType role;
} 