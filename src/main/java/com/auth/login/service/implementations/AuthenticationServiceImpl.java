package com.auth.login.service.implementations;

import com.auth.login.model.entities.User;
import com.auth.login.model.entities.VerificationCode;
import com.auth.login.model.enums.RoleType;
import com.auth.login.model.repository.UserRepository;
import com.auth.login.model.repository.VerificationCodeRepository;
import com.auth.login.web.dto.LoginUserDto;
import com.auth.login.web.dto.RegisterUserDto;
import com.auth.login.web.dto.ResetPasswordDto;
import jakarta.validation.Valid;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
SignUp, LogIn and ResetPassword implementation
*/

@Service
@Validated
public class AuthenticationServiceImpl {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final VerificationCodeRepository verificationCodeRepository;

    public AuthenticationServiceImpl(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            VerificationCodeRepository verificationCodeRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationCodeRepository = verificationCodeRepository;
    }

    /**
     * @param input
     * @return usuario creado al entity User
     */

    @Transactional
    public User signup(@Valid RegisterUserDto input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Debe estar autenticado como ADMINISTRADOR para crear usuarios.");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != RoleType.ADMINISTRADOR) {
            throw new AccessDeniedException("Solo un ADMINISTRADOR puede crear nuevos usuarios.");
        }

        User user = new User()
                .setFullName(input.getFullName())
                .setEmail(input.getEmail())
                .setPassword(passwordEncoder.encode(input.getPassword()))
                .setRole(input.getRole())
                .setNumberID(input.getNumberID());

        if (input.getRole() != RoleType.ADMINISTRADOR) {
            user.setAdmin(currentUser);
        }

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return userRepository.findByEmail(input.getEmail())
                .orElseThrow();
    }

    /**
     * Reset Password method requires being validated code sent to email, email and new password
     * @param resetPasswordDto
     */
    @Transactional
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        Optional<VerificationCode> storedCode = verificationCodeRepository.findByEmail(resetPasswordDto.getEmail());

        if (storedCode.isEmpty() || !storedCode.get().getCode().trim().equals(resetPasswordDto.getVerificationCode())) {
            throw new IllegalArgumentException("Código de verificación incorrecto");
        }

        verificationCodeRepository.deleteByEmail(resetPasswordDto.getEmail());

        User user = userRepository.findByEmail(resetPasswordDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (resetPasswordDto.getNewPassword() == null || resetPasswordDto.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
        userRepository.save(user);
    }
}

