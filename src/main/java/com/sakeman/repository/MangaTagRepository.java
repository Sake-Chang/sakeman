package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.MangaTag;


public interface MangaTagRepository extends JpaRepository<MangaTag, Integer> {
    List<MangaTag> findByMangaId(Integer mangaId);
    List<MangaTag> findByTagId(Integer authorId);
    Page<MangaTag> findAll(Pageable pageable);
    Optional<MangaTag> findByTagTagnameAndUserIdAndMangaId(String tagname, Integer userId, Integer mangaId);
}
