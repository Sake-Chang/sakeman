package com.sakeman.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;


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


}
