package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Like;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaFollow;
import com.sakeman.repository.LikeRepository;
import com.sakeman.repository.WebMangaFollowRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class WebMangaFollowService {

    private final WebMangaFollowRepository repository;

    /** Mangaで検索 */
    @Transactional(readOnly = true)
    public Optional<WebMangaFollow> findByReview(Manga manga) {
        return repository.findByManga(manga);
    }

    /** 個々のReviewのLike数をカウント */
    @Transactional(readOnly = true)
    public int countByManga(Manga manga){
        return repository.countByManga(manga);
    }

    /** UserとReviewで検索 */
    @Transactional(readOnly = true)
    public Optional<WebMangaFollow> findByUserAndManga(User user, Manga manga){
        return repository.findByUserAndManga(user, manga);
    }

    /** Userで検索 */
    @Transactional(readOnly = true)
    public List<WebMangaFollow> findByUser(User user) {
        return repository.findByUser(user);
    }

    /** Userで検索 */
    @Transactional(readOnly = true)
    public Page<WebMangaFollow> findByUserPageable(User user, Pageable pageable) {
        return repository.findByUser(user, pageable);
    }

    /** user_idで検索 */
    @Transactional(readOnly = true)
    public List<WebMangaFollow> findByUserId(Integer userId){
        return repository.findByUserId(userId);
    }

    /** ログインユーザーがLikeしているReviewのIDのリストを作成して返す */
    @Transactional(readOnly = true)
    public List<Integer> webMangaIdListLikedByUser(@AuthenticationPrincipal UserDetail userDetail){
        List<Integer> mangaIdList = new ArrayList<Integer>();
        if (userDetail != null) {
            /** ログインユーザーIDでLikeを取得 */
            List<WebMangaFollow> followlist = repository.findByUserId(userDetail.getUser().getId());

            /** ログインユーザーがWebMangaFollowしているMangaのIDを入れるリストを用意 */
            /** ログインユーザーがしているLikeのリストから順番にmangaIdを取得して新しいリストに追加 */
            /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
            followlist.forEach(i -> mangaIdList.add(i.getManga().getId()));
        }
        return mangaIdList;
    }

    /** 登録処理 */
    @Transactional
//    @CacheEvict(value = {"webMangaUpdateInfo"}, key = "'userId:' + #webMangaFollow.user.id")
    public WebMangaFollow saveWebMangaFollow(WebMangaFollow webMangaFollow) {
        System.out.println("登録");
        return repository.save(webMangaFollow);
    }

    /** 削除処理 */
    @Transactional
//    @CacheEvict(value = {"webMangaUpdateInfo"}, key = "'userId:' + #webMangaFollow.user.id", condition = "#webMangaFollow != null && #webMangaFollow.user != null")
    public void deleteById(Integer id) {
        WebMangaFollow webMangaFollow = repository.findById(id).orElse(null);
        if (webMangaFollow != null) {
            System.out.println("削除");
            repository.deleteById(id);
        }
    }

}
