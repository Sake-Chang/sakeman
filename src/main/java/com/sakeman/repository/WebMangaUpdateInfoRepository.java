package com.sakeman.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sakeman.entity.WebMangaUpdateInfo;


public interface WebMangaUpdateInfoRepository extends JpaRepository<WebMangaUpdateInfo, Integer> {
    Optional<WebMangaUpdateInfo> findByUrl(String url);
    List<WebMangaUpdateInfo> findByMangaId(Integer mangaId);
    Page<WebMangaUpdateInfo> findAll(Pageable pageable);
    Page<WebMangaUpdateInfo> findByMangaId(Integer mangaId, Pageable pageable);
    Page<WebMangaUpdateInfo> findByWebMangaMediaId(Integer mediaId, Pageable pageable);


    Page<WebMangaUpdateInfo> findByMangaIdIn(List<Integer> mangaIds, Pageable pageable);
    Page<WebMangaUpdateInfo> findByFreeFlag(Integer freeFlag, Pageable pageable);
    Page<WebMangaUpdateInfo> findByUpdateAtGreaterThanEqual(LocalDateTime today, Pageable pageable);
    List<WebMangaUpdateInfo> findByUpdateAtGreaterThanEqual(LocalDateTime today);

}
