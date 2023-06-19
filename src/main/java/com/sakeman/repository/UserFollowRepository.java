package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;


public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    public List<UserFollow> findByFollower(User follower);
    public UserFollow findByFollowerAndFollowee(User follower, User followee);
}
