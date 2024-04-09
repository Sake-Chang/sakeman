package com.sakeman.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;


public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    List<UserFollow> findByFollower(User follower);
    UserFollow findByFollowerAndFollowee(User follower, User followee);

    Page<UserFollow> findByFollowerIdOrderByRegisteredAt(Integer id, Pageable pageable);
    Page<UserFollow> findByFolloweeIdOrderByRegisteredAt(Integer id, Pageable pageable);
}
