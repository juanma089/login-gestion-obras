package com.auth.login.service.implementations;

import com.auth.login.model.entities.User;
import com.auth.login.model.enums.RoleType;
import com.auth.login.model.repository.UserRepository;
import com.auth.login.web.dto.RegisterUserDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @return all users created
     */
    public List<User> allUsers() {
        return new ArrayList<>(userRepository.findAll());
    }

    public List<User> getUsersByRole(RoleType role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(Integer userId, RegisterUserDto updatedUserDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean isAdmin = currentUser.getRole() == RoleType.ADMINISTRADOR;
        boolean isSelf = currentUser.getId().equals(userId);

        if (!isAdmin && !isSelf) {
            throw new RuntimeException("No tiene permisos para actualizar este usuario");
        }

        existingUser.setFullName(updatedUserDto.getFullName());
        existingUser.setEmail(updatedUserDto.getEmail());
        existingUser.setNumberID(updatedUserDto.getNumberID());

        if (isAdmin) {
            existingUser.setRole(updatedUserDto.getRole());
        }

        return userRepository.save(existingUser);
    }

    public List<User> obtenerUsuariosPorAdmin(Integer adminId) {
        return userRepository.findByAdminId(adminId);
    }

    public User getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public User getUserByNumberID(String numberID) {
        return userRepository.findByNumberID(numberID)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el número de identificación: " + numberID));
    }

    public boolean existsUserByNumberID(String numberID) {
        return userRepository.findByNumberID(numberID).isPresent();
    }

    public boolean existsUserByEmail(String numberID) {
        return userRepository.findByEmail(numberID).isPresent();
    }

    public List<User> getUsersByNumberIDs(List<String> numberIds) {
        return userRepository.findByNumberIDIn(numberIds);
    }


}
