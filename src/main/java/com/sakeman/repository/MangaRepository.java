package com.sakeman.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;


public interface MangaRepository extends JpaRepository<Manga, Integer> {
    Page<Manga> findAll(Pageable pageable);

}
