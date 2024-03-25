package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Like;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.entity.WebLike;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.LikeRepository;
import com.sakeman.repository.WebLikeRepository;


@Service
public class WebLikeService {

    private final WebLikeRepository webLikeRepository;

    public WebLikeService(WebLikeRepository repository) {
        this.webLikeRepository = repository;
    }

    /** Reviewで検索 */
    public Optional<WebLike> findByWebMangaUpdateInfo(WebMangaUpdateInfo info) {
        return webLikeRepository.findByWebMangaUpdateInfo(info);
    }

    /** 個々のReviewのLike数をカウント */
    public int countByWebMangaUpdateInfo(WebMangaUpdateInfo info){
        return webLikeRepository.countByWebMangaUpdateInfo(info);
    }

    /** UserとReviewで検索 */
    public Optional<WebLike> findByUserAndWebMangaUpdateInfo(User user, WebMangaUpdateInfo info){
        return webLikeRepository.findByUserAndWebMangaUpdateInfo(user, info);
    }

    /** Userで検索 */
    public List<WebLike> findByUser(User user) {
        return webLikeRepository.findByUser(user);
    }

    /** user_idで検索 */
    public List<WebLike> findByUserId(Integer userId){
        return webLikeRepository.findByUserId(userId);
    }

    /** ログインユーザーがLikeしているReviewのIDのリストを作成して返す */
    @Transactional
    public List<Integer> webMangaUpdateInfoIdListWebLikedByUser(@AuthenticationPrincipal UserDetail userDetail){
        List<Integer> webMangaUpdateInfoIdList = new ArrayList<>();
        if (userDetail != null) {
            /** ログインユーザーIDでLikeを取得 */
            List<WebLike> webLikes = webLikeRepository.findByUserId(userDetail.getUser().getId());
            /** ログインユーザーがLikeしているReviewのIDを入れるリストを用意 */
            /** ログインユーザーがしているLikeのリストから順番にreviewIdを取得して新しいリストに追加 */
            /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
            webLikes.forEach(i -> webMangaUpdateInfoIdList.add(i.getWebMangaUpdateInfo().getId()));
        }
        return webMangaUpdateInfoIdList;
    }



    /** 登録処理 */
    @Transactional
    public WebLike saveWebLike(WebLike webLike) {
        return webLikeRepository.save(webLike);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        webLikeRepository.deleteById(id);
    }

}
