package com.sakeman.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.WebMangaUpdateInfoRepository;


@Service
public class WebMangaUpdateInfoService {
    private final WebMangaUpdateInfoRepository webRepository;

    public WebMangaUpdateInfoService(WebMangaUpdateInfoRepository webRepository) {
        this.webRepository = webRepository;
    }

    /** 全件を検索して返す **/
    public List<WebMangaUpdateInfo> getUpdateInfoList() {
        return webRepository.findAll();
    }

    /** ページネーション */
    public Page<WebMangaUpdateInfo> getInfoListPageable(Pageable pageable){
        return webRepository.findAll(pageable);
    }

    /** 無料のみページネーション */
    public Page<WebMangaUpdateInfo> getFreeInfoListPageable(Integer freeFlag, Pageable pageable){
        return webRepository.findByFreeFlag(freeFlag, pageable);
    }

    /** 今日更新のみページネーション */
    public Page<WebMangaUpdateInfo> getTodayInfoListPageable(LocalDateTime today, Pageable pageable){
        return webRepository.findByUpdateAtGreaterThanEqual(today, pageable);
    }

    /** 今日更新のみ */
    public List<WebMangaUpdateInfo> getTodayInfoList(LocalDateTime today){
        return webRepository.findByUpdateAtGreaterThanEqual(today);
    }

    /** 1件を検索して返す */
    public WebMangaUpdateInfo getWebMangaUpdateInfo(Integer id) {
        return webRepository.findById(id).get();
    }

    /** Urlで検索して返す */
    public Optional<WebMangaUpdateInfo> findByUrl(String url) {
        return webRepository.findByUrl(url);
    }

    /** マンガIDで検索して返す */
    public List<WebMangaUpdateInfo> findByMangaId(Integer mangaId) {
        return webRepository.findByMangaId(mangaId);
    }

    /** マンガIDで検索して返す（ページ）  */
    public Page<WebMangaUpdateInfo> findByMangaIdPageable(Integer mangaId, Pageable pageable) {
        return webRepository.findByMangaId(mangaId, pageable);
    }

    /** マンガIDのリストで検索して返す */
    public Page<WebMangaUpdateInfo> findByMangaIdIn(List<Integer> mangaIds, Pageable pageable) {
        return webRepository.findByMangaIdIn(mangaIds, pageable);
    }

    /** メディアIDで検索して返す（ページ）  */
    public Page<WebMangaUpdateInfo> findByMediaIdPageable(Integer mediaId, Pageable pageable) {
        return webRepository.findByWebMangaMediaId(mediaId, pageable);
    }

    /** 登録処理 */
    @Transactional
    public WebMangaUpdateInfo saveInfo (WebMangaUpdateInfo info) {
        return webRepository.save(info);
    }

}
