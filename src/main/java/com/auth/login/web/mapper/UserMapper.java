package com.auth.login.web.mapper;

import com.auth.login.model.entities.User;
import com.auth.login.web.dto.UserResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto userToUserResponseDto(User user);
}