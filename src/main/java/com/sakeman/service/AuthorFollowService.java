package com.sakeman.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.AuthorFollow;
import com.sakeman.entity.User;
import com.sakeman.entity.UserFollow;
import com.sakeman.repository.AuthorFollowRepository;
import com.sakeman.repository.UserFollowRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class AuthorFollowService {

    private final AuthorFollowRepository afRepository;

    /** userで検索 */
    @Transactional(readOnly = true)
    public List<AuthorFollow> findByUser(User user) {
        return afRepository.findByUser(user);
    }

    /** user & authorで検索 */
    @Transactional(readOnly = true)
    public AuthorFollow findByUserAndAuthor(User user, Author author) {
        return afRepository.findByUserAndAuthor(user, author);
    }

    /** ログインユーザーがFollowしているUserのIDのリストを作成して返す */
    @Transactional(readOnly = true)
    public List<Integer> authorIdListFollowedByUser(@AuthenticationPrincipal UserDetail userDetail){
        List<AuthorFollow> followlist = new ArrayList<>();
        List<Integer> authorIdList = new ArrayList<Integer>();
        if (userDetail != null) {
            /** ログインユーザーIDでLikeを取得 */
            followlist = afRepository.findByUser(userDetail.getUser());

            /** ログインユーザーがしているLikeのリストから順番にreviewIdを取得して新しいリストに追加 */
            /** ログインユーザーがLikeしているReviewのIDのリストが完成 */
            followlist.forEach(i -> authorIdList.add(i.getAuthor().getId()));
        }

        return authorIdList;
    }

    /** 登録処理 */
    @Transactional
    public AuthorFollow save(AuthorFollow authorFollow) {
        return afRepository.save(authorFollow);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        afRepository.deleteById(id);
    }

}
