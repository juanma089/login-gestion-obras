package com.auth.login.web.controllers;

import com.auth.login.model.entities.User;
import com.auth.login.service.implementations.AuthenticationServiceImpl;
import com.auth.login.service.implementations.EmailServiceImpl;
import com.auth.login.service.implementations.JwtServiceImpl;
import com.auth.login.service.implementations.UserServiceImpl;
import com.auth.login.web.dto.*;
import com.auth.login.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * SignUp, Login and resetPassword endpoints
 */

@RequestMapping("/auth")
@RestController
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@Validated
public class AuthenticationController {
    private final JwtServiceImpl jwtService;
    private final EmailServiceImpl emailService;
    private final AuthenticationServiceImpl authenticationService;
    private final UserServiceImpl userService;
    private final UserMapper userMapper;

    public AuthenticationController(JwtServiceImpl jwtService,
                                    AuthenticationServiceImpl authenticationService,
                                    EmailServiceImpl emailService,
                                    UserServiceImpl userService,
                                    UserMapper userMapper) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.emailService = emailService;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /**
     * @Valid anotation is required to assure email pattern
     * Admin credentials are required to create a new user
     * @param registerUserDto
     */
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto registerUserDto) {
        boolean existsUserByEmail = userService.existsUserByEmail(registerUserDto.getEmail());
        boolean existsUserByNumberID = userService.existsUserByNumberID(registerUserDto.getNumberID());

        if(existsUserByEmail) {
            return ResponseEntity.status(400).body(Map.of("message", "Email ya esta registrado"));
        } else if(existsUserByNumberID){
            return ResponseEntity.status(400).body(Map.of("message", "Número de identificación ya esta registrado"));
        }

        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * JWT should be validated to use this endpoint
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {

        authenticationService.resetPassword(resetPasswordDto);
        return ResponseEntity.ok("Contraseña restablecida correctamente");
    }

    @PostMapping("/send-reset-code")
    public ResponseEntity<?> sendResetCode(@RequestParam String email) {
        if(!userService.existsUserByEmail(email))
            return ResponseEntity.status(404).body(Map.of("message", "Usuario no encontrado con el email proporcionado"));

        try {
            emailService.sendVerificationCode(email);
            return ResponseEntity.ok("Código enviado a " + email);
        } catch (MessagingException | UnsupportedEncodingException e) {
            return ResponseEntity.status(500).body("Error enviando correo");
        }
    }

}