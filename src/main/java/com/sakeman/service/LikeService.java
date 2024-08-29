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
import com.sakeman.repository.LikeRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;

    /** Reviewで検索 */
    @Transactional(readOnly = true)
    public Optional<Like> findByReview(Review review) {
        return likeRepository.findByReview(review);
    }

    /** 個々のReviewのLike数をカウント */
    @Transactional(readOnly = true)
    public int countByReview(Review review){
        return likeRepository.countByReview(review);
    }

    /** UserとReviewで検索 */
    @Transactional(readOnly = true)
    public Optional<Like> findByUserAndReview(User user, Review review){
        return likeRepository.findByUserAndReview(user, review);
    }

    /** Userで検索 */
    @Transactional(readOnly = true)
    public List<Like> findByUser(User user) {
        return likeRepository.findByUser(user);
    }

    /** user_idで検索 */
    @Transactional(readOnly = true)
    public List<Like> findByUserId(Integer userId){
        return likeRepository.findByUserId(userId);
    }

    /** ログインユーザーがLikeしているReviewのIDのリストを作成して返す */
    @Transactional(readOnly = true)
    public List<Integer> reviewIdListLikedByUser(@AuthenticationPrincipal UserDetail userDetail){
        List<Integer> reviewIdList = new ArrayList<>();
        if (userDetail != null) {
            /** ログインユーザーIDでLikeを取得 */
            List<Like> likes = likeRepository.findByUserId(userDetail.getUser().getId());

            /** ログインユーザーがLikeしているReviewのIDを入れるリストを用意 */
            /** ログインユーザーがしているLikeのリストから順番にreviewIdを取得して新しいリストに追加 */
            /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
            likes.forEach(i -> reviewIdList.add(i.getReview().getId()));
        }

        return reviewIdList;
    }



    /** 登録処理 */
    @Transactional
    public Like saveLike(Like like) {
        return likeRepository.save(like);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        likeRepository.deleteById(id);
    }

}
