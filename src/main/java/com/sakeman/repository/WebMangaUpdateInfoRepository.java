package com.sakeman.repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.entity.projection.webmanga.WebMangaUpdateInfoProjectionBasic;
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

    /** 昔の個別でやってた時のメソッド (ここから)*/
    /** 無料・フォロー・ジャンルで絞り込み */
    Page<WebMangaUpdateInfoProjectionBasic> findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagInAndMangaWebMangaFollowsUserId(List<Integer> genreIds, List<Integer> freeflags, Integer userId, Pageable pageable);
    /** 無料・フォローで絞り込み */
    Page<WebMangaUpdateInfoProjectionBasic> findDistinctByFreeFlagInAndMangaWebMangaFollowsUserId(List<Integer> freeflags, Integer userId, Pageable pageable);
    /** 無料・ジャンルで絞り込み */
    Page<WebMangaUpdateInfoProjectionBasic> findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagIn(List<Integer> genreIds, List<Integer> freeflags, Pageable pageable);
    /** 無料で絞り込み */
    Page<WebMangaUpdateInfoProjectionBasic> findByFreeFlagIn(List<Integer> freeflags, Pageable pageable);
    /** ノーマル */
    Page<WebMangaUpdateInfoProjectionBasic> findAllWithProjectionBy(Pageable pageable);
    /** 昔の個別でやってた時のメソッド (ここまで)*/

    @Query("""
            SELECT DISTINCT w
            FROM WebMangaUpdateInfo w
            JOIN w.manga m
            LEFT JOIN m.mangaTags mt
            LEFT JOIN mt.tag t
            LEFT JOIN t.genreTags gt
            WHERE
              ((:isGenreEmpty = true OR gt.genre.id IN :genreIds)
              AND w.freeFlag IN :freeflags
              AND (:followflag = 0 OR EXISTS (SELECT 1 FROM WebMangaFollow wf WHERE wf.user.id = :userId AND wf.manga.id = m.id)))
              OR (:oneshotflag = 1 AND m.isOneShot = true)
            """)
    Page<WebMangaUpdateInfoProjectionBasic> findFiltered(
        @Param("freeflags") List<Integer> freeflags,
        @Param("followflag") Integer followflag,
        @Param("userId") Integer userId,
        @Param("oneshotflag") Integer oneshotflag,
        @Param("genreIds") List<Integer> genreIds,
        @Param("isGenreEmpty") boolean isGenreEmpty,
        Pageable pageable);



    Optional<WebMangaUpdateInfo> findByTitleStringAndSubTitle(String titleString, String subTitle);

}
