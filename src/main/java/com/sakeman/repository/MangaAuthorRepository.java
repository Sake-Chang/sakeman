package com.sakeman.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.MangaAuthor;


public interface MangaAuthorRepository extends JpaRepository<MangaAuthor, Integer> {
    public List<MangaAuthor> findByMangaId(Integer mangaId);
}
