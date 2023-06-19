package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.repository.UserFollowRepository;


@Service
public class UserFollowService {

    private final UserFollowRepository ufRepository;

    public UserFollowService(UserFollowRepository repository) {
        this.ufRepository = repository;
    }

    /** followerで検索 */
    public List<UserFollow> findByFollower(User follower) {
        return ufRepository.findByFollower(follower);
    }

    /** follower & followeeで検索 */
    public UserFollow findByFollowerAndFollowee(User follower, User followee) {
        return ufRepository.findByFollowerAndFollowee(follower, followee);
    }

    /** ログインユーザーがFollowしているUserのIDのリストを作成して返す */
    public List<Integer> followeeIdListFollowedByUser(@AuthenticationPrincipal UserDetail userDetail){
        /** ログインユーザーIDでLikeを取得 */
        List<UserFollow> followlist = ufRepository.findByFollower(userDetail.getUser());

        /** ログインユーザーがLikeしているReviewのIDを入れるリストを用意 */
        List<Integer> followeeIdList = new ArrayList<Integer>();
        /** ログインユーザーがしているLikeのリストから順番にreviewIdを取得して新しいリストに追加 */
        /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
        followlist.forEach(i -> followeeIdList.add(i.getFollowee().getId()));

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
