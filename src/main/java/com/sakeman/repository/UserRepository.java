package com.sakeman.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    User findById(Integer id);
    Page<User> findAll(Pageable pageable);
    User findByVerificationToken(String verificationToken);

}
