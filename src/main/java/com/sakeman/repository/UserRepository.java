package com.sakeman.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    public User findByEmail(String email);
    public User findById(Integer id);

}
