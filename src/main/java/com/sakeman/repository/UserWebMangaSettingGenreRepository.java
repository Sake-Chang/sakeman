package com.sakeman.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Genre;
import com.sakeman.entity.UserWebMangaSetting;
import com.sakeman.entity.UserWebMangaSettingGenre;

public interface UserWebMangaSettingGenreRepository extends JpaRepository<UserWebMangaSettingGenre, Integer> {
    Optional<UserWebMangaSettingGenre> findByUserWebMangaSettingAndGenre(UserWebMangaSetting userWebMangaSetting, Genre genre);
}
