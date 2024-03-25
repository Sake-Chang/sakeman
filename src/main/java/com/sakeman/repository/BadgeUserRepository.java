package com.sakeman.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.BadgeUser;
import com.sakeman.entity.User;

public interface BadgeUserRepository extends JpaRepository<BadgeUser, Integer> {
    Page<BadgeUser> findAll(Pageable pageable);
    List<BadgeUser> findByUserId(Integer userId);
    Optional<BadgeUser> findByUserIdAndBadgeId(Integer uId, Integer bId);

}
