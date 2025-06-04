package com.auth.login.web.controllers;

/**
 *Unused class
 */

import com.auth.login.model.entities.User;
import com.auth.login.model.enums.RoleType;
import com.auth.login.service.implementations.AuthenticationServiceImpl;
import com.auth.login.service.implementations.UserServiceImpl;
import com.auth.login.web.dto.RegisterUserDto;
import com.auth.login.web.dto.UserResponseDto;
import com.auth.login.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserServiceImpl userService;
    private final AuthenticationServiceImpl authenticationService;
    private final UserMapper userMapper;

    public UserController(UserServiceImpl userService, AuthenticationServiceImpl authenticationService, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userMapper = userMapper;
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (!currentUser.getRole().name().equals("ADMINISTRADOR")) {
            return ResponseEntity.status(403).build();
        }

        List<User> users = userService.allUsers();
        List<UserResponseDto> response = users.stream()
                .map(userMapper::userToUserResponseDto)
                .toList();

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@PathVariable RoleType role) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) authentication.getPrincipal();

            if (currentUser.getRole() == RoleType.SUPERVISOR && role != RoleType.OPERADOR || currentUser.getRole() == RoleType.OPERADOR) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Collections.emptyList());
            }

            List<UserResponseDto> response = userService.getUsersByRole(role).stream()
                    .map(userMapper::userToUserResponseDto)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.emptyList());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Collections.emptyList());
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<?> authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        UserResponseDto response = userMapper.userToUserResponseDto(currentUser);

        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        UserResponseDto responseDto = userMapper.userToUserResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/identification/{numberID}")
    public ResponseEntity<UserResponseDto> getUserByNumberID(@PathVariable String numberID) {
        User user = userService.getUserByNumberID(numberID);
        UserResponseDto responseDto = userMapper.userToUserResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/batch")
    public ResponseEntity<List<UserResponseDto>> getUsersByIds(@RequestParam List<String> ids) {
        List<User> users = userService.getUsersByNumberIDs(ids);
        List<UserResponseDto> response = users.stream()
                .map(userMapper::userToUserResponseDto)
                .toList();
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Integer id,
            @RequestBody RegisterUserDto updatedUserDto) {
        User updatedUser = userService.updateUser(id, updatedUserDto);
        UserResponseDto responseDto = userMapper.userToUserResponseDto(updatedUser);
        return ResponseEntity.ok(responseDto);
    }
}

