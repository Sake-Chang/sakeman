package com.sakeman.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;


public interface UclistMangaRepository extends JpaRepository<UclistManga, Integer> {
    List<UclistManga> findByUclistId(Integer uclistId);
    Optional<UclistManga> findByUclistAndManga(Uclist uclist, Manga manga);

}
