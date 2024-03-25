package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Genre;
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;


public interface GenreTagRepository extends JpaRepository<GenreTag, Integer> {
    List<GenreTag> findByTagId(Integer tagId);
    List<GenreTag> findByGenreId(Integer genreId);
    Optional<GenreTag> findByGenreAndTag(Genre genre, Tag tag);

}
