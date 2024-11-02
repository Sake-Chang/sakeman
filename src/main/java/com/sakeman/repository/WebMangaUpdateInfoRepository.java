package com.sakeman.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.UserDetail;


public interface WebMangaUpdateInfoRepository extends JpaRepository<WebMangaUpdateInfo, Integer>, JpaSpecificationExecutor<WebMangaUpdateInfo> {
    Page<WebMangaUpdateInfo> findAll(Specification<WebMangaUpdateInfo> specs, Pageable pageable);

    Optional<WebMangaUpdateInfo> findByUrl(String url);
    List<WebMangaUpdateInfo> findByMangaId(Integer mangaId);
    Page<WebMangaUpdateInfo> findAll(Pageable pageable);
    Page<WebMangaUpdateInfo> findByMangaId(Integer mangaId, Pageable pageable);
    Page<WebMangaUpdateInfo> findByWebMangaMediaId(Integer mediaId, Pageable pageable);


    Page<WebMangaUpdateInfo> findByMangaIdIn(List<Integer> mangaIds, Pageable pageable);
    Page<WebMangaUpdateInfo> findByFreeFlag(Integer freeFlag, Pageable pageable);
    Page<WebMangaUpdateInfo> findByUpdateAtGreaterThanEqual(LocalDateTime today, Pageable pageable);
    List<WebMangaUpdateInfo> findByUpdateAtGreaterThanEqual(LocalDateTime today);

    Page<WebMangaUpdateInfo> findDistinctByMangaMangaTagsTagIdIn(List<Integer> tagIds, Pageable pageable);
    Page<WebMangaUpdateInfo> findDistinctByMangaMangaTagsTagGenreTagsGenreIdIn(List<Integer> genreIds, Pageable pageable);

    /** 有料無料・ジャンル・フォローで絞り込み */
    Page<WebMangaUpdateInfo> findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagInAndMangaWebMangaFollowsUserId(List<Integer> genreIds, List<Integer> freeflags, Integer userId, Pageable pageable);
    /** 有料無料・フォローで絞り込み */
    Page<WebMangaUpdateInfo> findDistinctByFreeFlagInAndMangaWebMangaFollowsUserId(List<Integer> freeflags, Integer userId, Pageable pageable);
    /** 有料無料・ジャンルで絞り込み */
    Page<WebMangaUpdateInfo> findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagIn(List<Integer> genreIds, List<Integer> freeflags, Pageable pageable);
    /** 有料無料で絞り込み */
    Page<WebMangaUpdateInfo> findByFreeFlagIn(List<Integer> freeflags, Pageable pageable);

    Optional<WebMangaUpdateInfo> findByTitleStringAndSubTitle(String titleString, String subTitle);

}
