package com.sakeman.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.WebMangaMedia;


public interface WebMangaMediaRepository extends JpaRepository<WebMangaMedia, Integer> {
    Page<WebMangaMedia> findAll(Pageable pageable);
    Optional<WebMangaMedia> findByName(String name);


}
