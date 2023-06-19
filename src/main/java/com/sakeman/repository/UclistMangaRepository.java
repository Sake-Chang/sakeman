package com.sakeman.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.UclistManga;


public interface UclistMangaRepository extends JpaRepository<UclistManga, Integer> {
    public List<UclistManga> findByUclistId(Integer uclistId);
}
