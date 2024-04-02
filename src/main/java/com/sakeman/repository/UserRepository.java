package com.sakeman.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findAll(Pageable pageable);
    Page<User> findByIsEnabled(boolean bool, Pageable pageable);

    User findById(Integer id);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);

}
