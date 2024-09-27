package com.sakeman.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sakeman.entity.Author;
import com.sakeman.entity.User;


public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findAll(Pageable pageable);
    Page<User> findByIsEnabled(boolean bool, Pageable pageable);

    @Query("SELECT uf.followee FROM UserFollow uf WHERE uf.follower.id = :userId")
    Page<User> findFollowingsByUserId(Integer userId, Pageable pageable);

    @Query("SELECT uf.follower FROM UserFollow uf WHERE uf.followee.id = :userId")
    Page<User> findFollowersByUserId(Integer userId, Pageable pageable);

    User findById(Integer id);
    Optional<User> findByEmail(String email);
    Optional<User> findByVerificationToken(String verificationToken);

    Page<User> findAll(Specification<User> spec, Pageable pageable);


}
