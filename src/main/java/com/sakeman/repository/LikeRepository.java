package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Like;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;


public interface LikeRepository extends JpaRepository<Like, Integer> {
    public Optional<Like> findByUserAndReview(User user, Review review);
    public List<Like> findByUser(User user);
    public List<Like> findByUserId(Integer userId);
    public Optional<Like> findByReview(Review review);
    public int countByReview(Review review);
}
