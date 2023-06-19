package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
    public List<Review> findByMangaId(Integer mangaId);
    Page<Review> findAll(Pageable pageable);

}
