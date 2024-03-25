package com.sakeman.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /* SELECT系 */
    /** 全件検索 **/
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    /** 全件検索 (ページング) **/
    public Page<User> getUserListPageable(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    /** ユーザーIDで検索 */
    public User getUser(Integer id) {
        return userRepository.findById(id);
    }

    /** Emailで検索 **/
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** verificationTokenで検索 **/
    public User getByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken);
    }

    /** あいまい検索 (Select2で使用) */
    @Transactional
    public List<User> getSearchResult(User user) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return userRepository.findAll(Example.of(user, matcher));
    }

    public String getVerificationLink(HttpServletRequest request, String token) {
        // ベースURLの取得
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // リンクの構築
        return baseUrl + "/verify?token=" + token;
    }

    public String getPasswordResetLink(HttpServletRequest request, String token) {
        // ベースURLの取得
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // リンクの構築
        return baseUrl + "/password-reset/reset-form?token=" + token;
    }

    /* CREATE・UPDATE系 */
    /** ユーザーの新規登録 */
    @Transactional
    public User saveUser (User user) {
        return userRepository.save(user);
    }

    /** Webまんがのマイセット登録 **/
    @Transactional
    public User saveSettings(UserDetail userDetail, List<Integer> genres, Integer freeflag, Integer followflag) {
        User user = userDetail.getUser();
        user.setWebMangaSettingsGenre(genres);
        user.setWebMangaSettingsFreeflag(freeflag);
        user.setWebMangaSettingsFollowflag(followflag);
        return userRepository.save(user);
    }


    /** 認証系 **/
    // トークンが有効であるかを確認
    public boolean isValidUserToken(User user) {
        return user != null &&  //ユーザーが存在しない
                User.VerificationTokenStatus.VALID.equals(user.getVerificationTokenStatus()) && //トークンが無効
                user.getVerificationTokenExpiration() != null &&    //トークンが期限切れ
                !new Date().after(user.getVerificationTokenExpiration());   //
    }

}
