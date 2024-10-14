package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;


public interface MangaAuthorRepository extends JpaRepository<MangaAuthor, Integer> {
    List<MangaAuthor> findByMangaId(Integer mangaId);
    List<MangaAuthor> findByAuthorId(Integer authorId);
    Page<MangaAuthor> findAll(Pageable pageable);
    Optional<MangaAuthor> findByMangaAndAuthor(Manga manga, Author author);

    @Query("SELECT ma FROM MangaAuthor ma " +
            "JOIN ma.manga m " +
            "JOIN ma.author a " +
            "WHERE m.deleteFlag = 0 " +
            "AND a.deleteFlag = 0 ")
     Page<MangaAuthor> findAllActive(Pageable pageable);
}
