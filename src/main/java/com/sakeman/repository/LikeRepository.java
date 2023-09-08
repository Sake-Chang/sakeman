package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Like;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;


public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByUserAndReview(User user, Review review);
    List<Like> findByUser(User user);
    List<Like> findByUserId(Integer userId);
    Optional<Like> findByReview(Review review);
    int countByReview(Review review);
}
