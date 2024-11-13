package com.sakeman.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByMangaId(Integer mangaId);
    Page<Review> findByMangaId(Integer id, Pageable pageable);
    List<Review> findFirst5ByMangaId(Integer mangaId);

    Page<Review> findAll(Pageable pageable);
    Page<Review> findDistinctByMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable);

    List<Review> findTop10ByOrderByRegisteredAtDesc();


}
