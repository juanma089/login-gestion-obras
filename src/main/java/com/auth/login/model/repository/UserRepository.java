package com.auth.login.model.repository;

import com.auth.login.model.entities.User;
import com.auth.login.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    List<User> findByAdminId(Integer adminId);
    Optional<User> findByNumberID(String numberID);
    List<User> findByNumberIDIn(List<String> numberIds);
    List<User> findByRole(RoleType role);

}