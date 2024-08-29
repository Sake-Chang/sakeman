package com.sakeman.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Manga;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.WebMangaUpdateInfoRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WebMangaUpdateInfoService {
    private final WebMangaUpdateInfoRepository webRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<WebMangaUpdateInfo> getUpdateInfoList() {
        return webRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getInfoListPageable(Pageable pageable){
        return webRepository.findAll(pageable);
    }

    /** 有料無料・ジャンル・フォローで絞り込み */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getFilteredInfoListPageable(List<Integer> genreIds, List<Integer> freeflags, Integer userId, Pageable pageable){
        Page<WebMangaUpdateInfo> res = webRepository.findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagInAndMangaWebMangaFollowsUserId(genreIds, freeflags, userId, pageable);
        return res;
    }
    /** 有料無料・フォローで絞り込み */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getFilteredInfoListPageable(List<Integer> freeflags, Integer userId, Pageable pageable){
        Page<WebMangaUpdateInfo> res = webRepository.findDistinctByFreeFlagInAndMangaWebMangaFollowsUserId(freeflags, userId, pageable);
        return res;
    }
    /** 有料無料・ジャンルで絞り込み */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getFilteredInfoListPageable(List<Integer> genreIds, List<Integer> freeflags, Pageable pageable){
        Page<WebMangaUpdateInfo> res = webRepository.findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagIn(genreIds, freeflags, pageable);
        return res;
    }
    /** 有料無料で絞り込み */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getFilteredInfoListPageable(List<Integer> freeflags, Pageable pageable){
        Page<WebMangaUpdateInfo> res = webRepository.findByFreeFlagIn(freeflags, pageable);
        return res;
    }

    /** 無料のみページネーション */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getFreeInfoListPageable(Integer freeFlag, Pageable pageable){
        return webRepository.findByFreeFlag(freeFlag, pageable);
    }

    /** 今日更新のみページネーション */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getTodayInfoListPageable(LocalDateTime today, Pageable pageable){
        return webRepository.findByUpdateAtGreaterThanEqual(today, pageable);
    }

    /** 今日更新のみ */
    @Transactional(readOnly = true)
    public List<WebMangaUpdateInfo> getTodayInfoList(LocalDateTime today){
        return webRepository.findByUpdateAtGreaterThanEqual(today);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public WebMangaUpdateInfo getWebMangaUpdateInfo(Integer id) {
        return webRepository.findById(id).get();
    }

    /** Urlで検索して返す */
    @Transactional(readOnly = true)
    public Optional<WebMangaUpdateInfo> findByUrl(String url) {
        return webRepository.findByUrl(url);
    }

    /** マンガIDで検索して返す */
    @Transactional(readOnly = true)
    public List<WebMangaUpdateInfo> findByMangaId(Integer mangaId) {
        return webRepository.findByMangaId(mangaId);
    }

    /** マンガIDで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> findByMangaIdPageable(Integer mangaId, Pageable pageable) {
        return webRepository.findByMangaId(mangaId, pageable);
    }

    /** マンガIDのリストで検索して返す */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> findByMangaIdIn(List<Integer> mangaIds, Pageable pageable) {
        return webRepository.findByMangaIdIn(mangaIds, pageable);
    }

    /** メディアIDで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> findByMediaIdPageable(Integer mediaId, Pageable pageable) {
        return webRepository.findByWebMangaMediaId(mediaId, pageable);
    }

    /** 作品タイトルとサブタイトルで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    public Optional<WebMangaUpdateInfo> findByTitleStringAndSubTitle(String titleString, String subTitle) {
        return webRepository.findByTitleStringAndSubTitle(titleString, subTitle);
    }


    /** 登録処理 */
    @Transactional
    public WebMangaUpdateInfo saveInfo (WebMangaUpdateInfo info) {
        return webRepository.save(info);
    }

}
