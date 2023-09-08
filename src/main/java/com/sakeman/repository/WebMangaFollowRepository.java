package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaFollow;


public interface WebMangaFollowRepository extends JpaRepository<WebMangaFollow, Integer> {
    Optional<WebMangaFollow> findByUserAndManga(User user, Manga manga);
    List<WebMangaFollow> findByUser(User user);
    List<WebMangaFollow> findByUserId(Integer userId);
    Optional<WebMangaFollow> findByManga(Manga manga);
    int countByManga(Manga manga);
}
