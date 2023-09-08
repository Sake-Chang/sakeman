package com.sakeman.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.MangaAuthor;


public interface MangaAuthorRepository extends JpaRepository<MangaAuthor, Integer> {
    List<MangaAuthor> findByMangaId(Integer mangaId);
    List<MangaAuthor> findByAuthorId(Integer authorId);
    Page<MangaAuthor> findAll(Pageable pageable);
}
