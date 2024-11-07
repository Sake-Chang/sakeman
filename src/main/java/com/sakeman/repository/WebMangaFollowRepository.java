package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaFollow;
import com.sakeman.entity.WebMangaUpdateInfo;


public interface WebMangaFollowRepository extends JpaRepository<WebMangaFollow, Integer> {
    Optional<WebMangaFollow> findByUserAndManga(User user, Manga manga);
    List<WebMangaFollow> findByUser(User user);
    List<WebMangaFollow> findByUserId(Integer userId);
    Optional<WebMangaFollow> findByManga(Manga manga);
    int countByManga(Manga manga);
    Page<WebMangaFollow> findByUser(User user, Pageable pageable);

}
