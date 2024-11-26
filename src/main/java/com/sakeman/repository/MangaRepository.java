package com.sakeman.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.ReadStatus;


public interface MangaRepository extends JpaRepository<Manga, Integer> {
    Page<Manga> findAll(Pageable pageable);
    Optional<Manga> findByTitle(String title);
    Optional<Manga> findById(Integer id);
    List<Manga> findByTitleLike(String string);
    List<Manga> findByTitleCleanse(String titleCleanse);
    List<Manga> findByIdBetween(int start, int end);

    List<Manga> findByMangaAuthorsAuthorId(Integer authorId);
    Page<Manga> findByMangaAuthorsAuthorId(Integer authorId, Pageable pageable);
    List<Manga> findByMangaAuthorsAuthorIn(List<Author> authors);

    Page<Manga> findDistinctByMangaAuthorsAuthorIn(List<Author> authors, Pageable pageable);

    Page<Manga> findAll(Specification<Manga> spec, Pageable pageable);

    Page<Manga> findByReadStatusUserIdAndReadStatusStatus(Integer userId, ReadStatus.Status status, Pageable pageable);
    Page<Manga> findByWebMangaFollowsUserId(Integer userId, Pageable pageable);

    @Query("""
            SELECT m
            FROM Manga m
            JOIN m.readStatus rs
            LEFT JOIN m.reviews r
            WHERE rs.user.id = :userId
              AND rs.status = :status
              AND (r.user.id = :userId OR r.user IS NULL)
            ORDER BY r.rating DESC, rs.updatedAt DESC
        """)
    Page<Manga> findByReadStatusUserIdAndReadStatusStatusSortByRating(Integer userId, ReadStatus.Status status, Pageable pageable);

    @Query("""
            SELECT m
            FROM Manga m
            JOIN m.webMangaFollows wf
            LEFT JOIN m.reviews r
            WHERE wf.user.id = :userId
              AND (r.user.id = :userId OR r.user IS NULL)
            ORDER BY r.rating DESC, wf.updatedAt DESC
        """)
    Page<Manga> findByWebMangaFollowsUserIdSortByRating(Integer userId, Pageable pageable);

}
