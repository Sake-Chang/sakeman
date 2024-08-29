package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.repository.UserFollowRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserFollowService {

    private final UserFollowRepository ufRepository;

    /** followerで検索 */
    @Transactional(readOnly = true)
    public List<UserFollow> findByFollower(User follower) {
        return ufRepository.findByFollower(follower);
    }

    /** follower_id で検索 */
    @Transactional(readOnly = true)
    public Page<UserFollow> findByFollowerIdOrderByRegisteredAt(Integer id, Pageable pageable) {
        return ufRepository.findByFollowerIdOrderByRegisteredAt(id, pageable);
    }

    /** followee_id で検索 */
    @Transactional(readOnly = true)
    public Page<UserFollow> findByFolloweeIdOrderByRegisteredAt(Integer id, Pageable pageable) {
        return ufRepository.findByFolloweeIdOrderByRegisteredAt(id, pageable);
    }

    /** follower & followeeで検索 */
    @Transactional(readOnly = true)
    public UserFollow findByFollowerAndFollowee(User follower, User followee) {
        return ufRepository.findByFollowerAndFollowee(follower, followee);
    }

    /** ログインユーザーがFollowしているUserのIDのリストを作成して返す */
    @Transactional(readOnly = true)
    public List<Integer> followeeIdListFollowedByUser(@AuthenticationPrincipal UserDetail userDetail){
        List<Integer> followeeIdList = new ArrayList<>();
        if (userDetail != null) {
            /** ログインユーザーIDでLikeを取得 */
            List<UserFollow> followlist = ufRepository.findByFollower(userDetail.getUser());

            /** ログインユーザーがLikeしているReviewのIDを入れるリストを用意 */
            /** ログインユーザーがしているLikeのリストから順番にreviewIdを取得して新しいリストに追加 */
            /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
            followlist.forEach(i -> followeeIdList.add(i.getFollowee().getId()));
        }
        return followeeIdList;
    }

    /** 登録処理 */
    @Transactional
    public UserFollow saveUserFollow(UserFollow userFollow) {
        return ufRepository.save(userFollow);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        ufRepository.deleteById(id);
    }

}
