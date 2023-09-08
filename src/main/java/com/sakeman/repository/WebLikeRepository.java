package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.User;
import com.sakeman.entity.WebLike;
import com.sakeman.entity.WebMangaUpdateInfo;


public interface WebLikeRepository extends JpaRepository<WebLike, Integer> {
    Optional<WebLike> findByUserAndWebMangaUpdateInfo(User user, WebMangaUpdateInfo info);
    List<WebLike> findByUser(User user);
    List<WebLike> findByUserId(Integer userId);
    Optional<WebLike> findByWebMangaUpdateInfo(WebMangaUpdateInfo info);
    int countByWebMangaUpdateInfo(WebMangaUpdateInfo info);
}
