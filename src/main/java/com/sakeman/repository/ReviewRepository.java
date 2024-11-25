package com.sakeman.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sakeman.entity.Review;


public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByMangaId(Integer mangaId);
    Page<Review> findByMangaId(Integer id, Pageable pageable);
    List<Review> findFirst5ByMangaId(Integer mangaId);

    Page<Review> findAll(Pageable pageable);
    Page<Review> findDistinctByMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable);

    List<Review> findTop10ByOrderByRegisteredAtDesc();

    Optional<Review> findByUserIdAndMangaId(Integer userId, Integer mangaId);

    Page<Review> findByTitleIsNotNullOrContentIsNotNull(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.manga.id = :mangaId AND (r.title IS NOT NULL OR r.content IS NOT NULL)")
    Page<Review> findByMangaIdAndTitleIsNotNullOrContentIsNotNull(Integer mangaId, Pageable pageable);


}
