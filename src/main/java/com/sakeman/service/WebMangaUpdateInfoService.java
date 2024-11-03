package com.sakeman.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sakeman.dto.WebMangaUpdateInfoAdminResponseDTO;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.entity.projection.webmanga.WebMangaUpdateInfoProjectionBasic;
import com.sakeman.repository.WebMangaUpdateInfoRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WebMangaUpdateInfoService {
    private final WebMangaUpdateInfoRepository webRepository;
    private final MangaService maService;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfo", key = "'allEntries'", condition = "#useCache")
    public List<WebMangaUpdateInfo> getUpdateInfoList(boolean useCache) {
        return webRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfo", key = "'page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> getInfoListPageable(Pageable pageable, boolean useCache){
        return webRepository.findAll(pageable);
    }

    /** ページネーション Projection */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoProjection", key = "'page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfoProjectionBasic> getInfoListPageableProjection(Pageable pageable, boolean useCache){
        return webRepository.findAllWithProjectionBy(pageable);
    }

    /** ページネーション(キャッシュなし) */
    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getInfoListPageable(Pageable pageable){
        return webRepository.findAll(pageable);
    }

//    /** 追加したspecification用のメソッド */
//    @Transactional(readOnly = true)
//    public Page<WebMangaUpdateInfoProjectionBasic> getFilteredInfoListPageableSpecific(
//            User thisUser, Integer freeflag, Integer followflag, Integer oneshotflag, List<Integer> genreSetting, Pageable pageable, boolean useCache) {
//
//        // 基本条件に基づいてデータを取得
//        List<WebMangaUpdateInfoProjectionBasic> basicResultList = getBasicFilteredResult(thisUser, freeflag, followflag, oneshotflag, genreSetting, pageable);
//        // OneShotのみを取得
////        List<WebMangaUpdateInfo> oneShotResultList = getOneShotResult(oneshotflag, pageable);
//
//        /** basicResultListをpageableで指定されたページサイズに合わせてサブリストに分割 */
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), basicResultList.size());
//        List<WebMangaUpdateInfoProjectionBasic> paginatedList = basicResultList.subList(start, end);
//
//        /** PageImplを使用してPageオブジェクトを作成 */
//        return new PageImpl<>(paginatedList, pageable, basicResultList.size());
//
////        // 結果をマージして重複を排除
////        LinkedHashSet<WebMangaUpdateInfo> mergedResults = new LinkedHashSet<>(basicResultList);
////        mergedResults.addAll(oneShotResultList);
////
////        List<WebMangaUpdateInfo> mergedList = new ArrayList<>(mergedResults);
////        int start = (int) pageable.getOffset();
////        int end = Math.min((start + pageable.getPageSize()), mergedList.size());
////
////        // ページングされた結果を返す
////        return new PageImpl<>(mergedList.subList(start, end), pageable, mergedList.size());
//    }
//
//
//    public List<WebMangaUpdateInfoProjectionBasic> getBasicFilteredResult(User thisUser, Integer freeflag, Integer followflag, Integer oneshotflag, List<Integer> genreSetting, Pageable pageable) {
//        Specification<WebMangaUpdateInfoProjectionBasic> specs;
//        if (thisUser != null) {
//            specs = Specification
//                        .where(WebMangaUpdateInfoSpecifications.hasGenres(genreSetting))
//                        .and(WebMangaUpdateInfoSpecifications.hasFreeFlags(freeflag))
//                        .and(WebMangaUpdateInfoSpecifications.followedByUser(followflag, thisUser.getId()))
////                        .or(WebMangaUpdateInfoSpecifications.isOneShot(oneshotflag))
//                        ;
////            if (oneshotflag != null && oneshotflag == 1) {
////                specs = specs.or(WebMangaUpdateInfoSpecifications.isOneShot(oneshotflag));
////            }
//        } else {
//            specs = Specification.where(null);
//        }
//
//        return webRepository.findAllProjection(specs, pageable.getSort());
//    }
//
//    public List<WebMangaUpdateInfoProjectionBasic> getOneShotResult(Integer oneshotflag, Pageable pageable) {
//        if (oneshotflag == null || oneshotflag != 1) {
//            return Collections.emptyList();
//        }
//        Specification<WebMangaUpdateInfoProjectionBasic> oneShotSpecs = WebMangaUpdateInfoSpecifications.isOneShot(oneshotflag);
//        return webRepository.findAllProjection(oneShotSpecs, pageable.getSort());
//    }


    @Transactional(readOnly=true)
    public Page<WebMangaUpdateInfoProjectionBasic> getFiltered(List<Integer> genreIds, boolean isGenreEmpty, List<Integer> freeflags, Integer followflag, Integer userId, Integer oneshotflag, Pageable pageable, boolean useCache) {
        return webRepository.findFiltered(freeflags, followflag, userId, oneshotflag, genreIds, isGenreEmpty, pageable);
    }

/** 従前の個別のメソッド( getFilteredInfoListPageable ) ここから */
    /** 有料無料・ジャンル・フォローで絞り込み */
    @Transactional(readOnly = true)
//    @Cacheable(value = "webMangaUpdateInfo", key = "'genre:' + #genreIds + ',freeflag:' + #freeflags + ',userId:' + #userId + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfoProjectionBasic> getFilteredInfoListPageable(List<Integer> genreIds, List<Integer> freeflags, Integer userId, Pageable pageable, boolean useCache){
        Page<WebMangaUpdateInfoProjectionBasic> res = webRepository.findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagInAndMangaWebMangaFollowsUserId(genreIds, freeflags, userId, pageable);
        return res;
    }
    /** 有料無料・フォローで絞り込み */
    @Transactional(readOnly = true)
//    @Cacheable(value = "webMangaUpdateInfo", key = "'freeflag:' + #freeflags + ',userId:' + #userId + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfoProjectionBasic> getFilteredInfoListPageable(List<Integer> freeflags, Integer userId, Pageable pageable, boolean useCache){
        Page<WebMangaUpdateInfoProjectionBasic> res = webRepository.findDistinctByFreeFlagInAndMangaWebMangaFollowsUserId(freeflags, userId, pageable);
        return res;
    }
    /** 有料無料・ジャンルで絞り込み */
    @Transactional(readOnly = true)
//    @Cacheable(value = "webMangaUpdateInfo", key = "'genre:' + #genreIds + ',freeflag:' + #freeflags + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfoProjectionBasic> getFilteredInfoListPageable(List<Integer> genreIds, List<Integer> freeflags, Pageable pageable, boolean useCache){
        Page<WebMangaUpdateInfoProjectionBasic> res = webRepository.findDistinctByMangaMangaTagsTagGenreTagsGenreIdInAndFreeFlagIn(genreIds, freeflags, pageable);
        return res;
    }
    /** 有料無料で絞り込み */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfo", key = "'freeflag:' + #freeflags + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfoProjectionBasic> getFilteredInfoListPageable(List<Integer> freeflags, Pageable pageable, boolean useCache){
        Page<WebMangaUpdateInfoProjectionBasic> res = webRepository.findByFreeFlagIn(freeflags, pageable);
        return res;
    }
/** 従前の個別のメソッド( getFilteredInfoListPageable ) ここまで */

    /** 無料のみページネーション */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfo", key = "'freeflag:' + #freeflags + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> getFreeInfoListPageable(Integer freeFlag, Pageable pageable, boolean useCache){
        return webRepository.findByFreeFlag(freeFlag, pageable);
    }

    /** 今日更新のみページネーション */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoToday", key = "'today:' + #today.toLocalDate() + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> getTodayInfoListPageable(LocalDateTime today, Pageable pageable, boolean useCache){
        return webRepository.findByUpdateAtGreaterThanEqual(today, pageable);
    }

    /** 今日更新のみ */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoToday", key = "'today:' + #today.toLocalDate()", condition = "#useCache")
    public List<WebMangaUpdateInfo> getTodayInfoList(LocalDateTime today, boolean useCache){
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
    @Cacheable(value = "webMangaUpdateInfoByMangaId", key = "'mangaId:' + #mangaId", condition = "#useCache")
    public List<WebMangaUpdateInfo> findByMangaId(Integer mangaId, boolean useCache) {
        return webRepository.findByMangaId(mangaId);
    }

    /** マンガIDで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoByMangaId", key = "'mangaId:' + #mangaId + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> findByMangaIdPageable(Integer mangaId, Pageable pageable, boolean useCache) {
        return webRepository.findByMangaId(mangaId, pageable);
    }

    /** マンガIDのリストで検索して返す */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoByMangaId", key = "'mangaIds:' + #mangaIds.toString() + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> findByMangaIdIn(List<Integer> mangaIds, Pageable pageable, boolean useCache) {
        return webRepository.findByMangaIdIn(mangaIds, pageable);
    }

    /** メディアIDで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoByMediaId", key = "'mediaId:' + #mediaId + ',page:' + #pageable.pageNumber + ',size:' + #pageable.pageSize", condition = "#useCache")
    public Page<WebMangaUpdateInfo> findByMediaIdPageable(Integer mediaId, Pageable pageable, boolean useCache) {
        return webRepository.findByWebMangaMediaId(mediaId, pageable);
    }

    /** 作品タイトルとサブタイトルで検索して返す（ページ）  */
    @Transactional(readOnly = true)
    @Cacheable(value = "webMangaUpdateInfoByTitleSubtitle", key = "'title:' + #titleString + ',subtitle:' + #subTitle", condition = "#useCache")
    public Optional<WebMangaUpdateInfo> findByTitleStringAndSubTitle(String titleString, String subTitle, boolean useCache) {
        return webRepository.findByTitleStringAndSubTitle(titleString, subTitle);
    }

    @Transactional(readOnly = true)
    public Page<WebMangaUpdateInfo> getSearchResult(WebMangaUpdateInfo webMangaUpdateInfo, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return webRepository.findAll(Example.of(webMangaUpdateInfo, matcher), pageable);
    }

    /** Admin用検索結果 */
    public Page<WebMangaUpdateInfo> searchWebMangaUpdateInfos(String searchValue, Pageable pageable) {
        Page<WebMangaUpdateInfo> pageData;
        Integer mangaId = 111111;

        if (StringUtils.hasText(searchValue)) {
            WebMangaUpdateInfo webMangaUpdateInfo = new WebMangaUpdateInfo();
            webMangaUpdateInfo.setTitleString(searchValue);
            webMangaUpdateInfo.setManga(maService.getManga(mangaId));
            pageData = getSearchResult(webMangaUpdateInfo, pageable);
        } else {
            pageData = findByMangaIdPageable(mangaId, pageable, false);
        }
        return pageData;
    }

    public List<WebMangaUpdateInfoAdminResponseDTO> getResponseData(Page<WebMangaUpdateInfo> pageData) {
        return pageData.getContent().stream().map(m -> {
            return new WebMangaUpdateInfoAdminResponseDTO(
                m.getId(),
                m.getMediaName(),
                m.getWebMangaMedia().getId(),
                m.getTitleString(),
                m.getManga().getId(),
                m.getSubTitle(),
                m.getAuthorString()
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return webRepository.count();
    }


    /** 登録処理 */
    @Transactional
    @CacheEvict(value = {
            "webMangaUpdateInfo",
            "webMangaUpdateInfoToday",
            "webMangaUpdateInfoByMangaId",
            "webMangaUpdateInfoByMediaId",
            "webMangaUpdateInfoByTitleSubtitle"
        }, allEntries = true)
    public WebMangaUpdateInfo saveInfo (WebMangaUpdateInfo info) {
        return webRepository.save(info);
    }

    @Transactional
    @CacheEvict(value = {
            "webMangaUpdateInfo",
            "webMangaUpdateInfoToday",
            "webMangaUpdateInfoByMangaId",
            "webMangaUpdateInfoByMediaId",
            "webMangaUpdateInfoByTitleSubtitle"
        }, allEntries = true)
    public void deleteById(Integer id) {
        webRepository.deleteById(id);
    }

}
