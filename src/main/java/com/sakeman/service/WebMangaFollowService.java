package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


@Service
public class WebMangaFollowService {

    private final WebMangaFollowRepository repository;

    public WebMangaFollowService(WebMangaFollowRepository repository) {
        this.repository = repository;
    }

    /** Mangaで検索 */
    public Optional<WebMangaFollow> findByReview(Manga manga) {
        return repository.findByManga(manga);
    }

    /** 個々のReviewのLike数をカウント */
    public int countByManga(Manga manga){
        return repository.countByManga(manga);
    }

    /** UserとReviewで検索 */
    public Optional<WebMangaFollow> findByUserAndManga(User user, Manga manga){
        return repository.findByUserAndManga(user, manga);
    }

    /** Userで検索 */
    public List<WebMangaFollow> findByUser(User user) {
        return repository.findByUser(user);
    }

    /** user_idで検索 */
    public List<WebMangaFollow> findByUserId(Integer userId){
        return repository.findByUserId(userId);
    }

    /** ログインユーザーがLikeしているReviewのIDのリストを作成して返す */
    @Transactional
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
    public WebMangaFollow saveWebMangaFollow(WebMangaFollow webMangaFollow) {
        return repository.save(webMangaFollow);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

}
