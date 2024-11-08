package com.sakeman.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Genre;
import com.sakeman.entity.Review;
import com.sakeman.entity.User;
import com.sakeman.entity.UserWebMangaSetting;
import com.sakeman.entity.UserWebMangaSettingGenre;
import com.sakeman.repository.GenreRepository;
import com.sakeman.repository.UserRepository;
import com.sakeman.repository.UserWebMangaSettingGenreRepository;
import com.sakeman.repository.UserWebMangaSettingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GenreRepository genreRepository;
    private final UserWebMangaSettingRepository settingRepository;
    private final UserWebMangaSettingGenreRepository settingGenreRepository;

    /* SELECT系 */
    /** 全件検索 **/
    @Transactional(readOnly = true)
    public List<User> getUserList() {
        return userRepository.findAll();
    }

    /** 全件検索 (ページング) **/
    @Transactional(readOnly = true)
    public Page<User> getUserListPageable(Pageable pageable){
        return userRepository.findAll(pageable);
    }

    /** 全件検索 (ページング) **/
    @Transactional(readOnly = true)
    public Page<User> getEnabledUserListPageable(boolean bool, Pageable pageable){
        return userRepository.findByIsEnabled(bool, pageable);
    }

    /** ユーザーIDで検索 */
    @Transactional(readOnly = true)
    public User getUser(Integer id) {
        return userRepository.findById(id);
    }

    /** Emailで検索 **/
    @Transactional(readOnly = true)
    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /** verificationTokenで検索 **/
    @Transactional(readOnly = true)
    public Optional<User> getByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken);
    }

    /** あいまい検索 (Select2で使用) */
    @Transactional(readOnly = true)
    public List<User> getSearchResult(User user) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return userRepository.findAll(Example.of(user, matcher));
    }

    @Transactional(readOnly = true)
    public Page<User> getSearchResultWithPaging(User user, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方

        return userRepository.findAll(Example.of(user, matcher), pageable);
    }

    /** フォローしている人の検索 */
    @Transactional(readOnly = true)
    public Page<User> getFollowingsByUserId(Integer id, Pageable pageable) {
        return userRepository.findFollowingsByUserId(id, pageable);
    }

    /** フォローされている人の検索 */
    @Transactional(readOnly = true)
    public Page<User> getFollowersByUserId(Integer id, Pageable pageable) {
        return userRepository.findFollowersByUserId(id, pageable);
    }

    /** ユーザーの保存 */
    @Transactional
    public User saveUser (User user) {
        return userRepository.save(user);
    }

    /** Webまんがのマイセット登録 **/
    @Transactional
    public User saveSettings(UserDetail userDetail, List<Integer> genres, Integer freeflag, Integer followflag, Integer oneshotflag) {
        User user = userDetail.getUser();
        UserWebMangaSetting setting;
        if (user.getUserWebMangaSetting() != null) {
            setting = settingRepository.findById(user.getUserWebMangaSetting().getId()).orElse(new UserWebMangaSetting());
        } else {
            setting = new UserWebMangaSetting();
        }

        List<Integer> currentGenreIds = user.getGenreIdsAll();

        List<UserWebMangaSettingGenre> newList = new ArrayList<>();
        // 送られてきたgenresを1つずつ確認
        for (Integer genreId : genres) {
            // 既存セットにあった場合
            if (currentGenreIds.contains(genreId)) {
                // そのUserWebMangaSetingGenreを取得
                Optional<UserWebMangaSettingGenre> OptionalItem = settingGenreRepository.findByUserWebMangaSettingAndGenre(setting, genreRepository.findById(genreId).get());
                if (OptionalItem.isPresent()) {
                    UserWebMangaSettingGenre item = OptionalItem.get();
                    // delete_flag==trueの場合falseに変更
                    if (item.isDeleteFlag()) {
                        item.setDeleteFlag(false);
                    }
                    newList.add(item);
                } else {
                    // ここには到達しないはずですが、安全のため
                    throw new IllegalStateException("Unexpected state: UserWebMangaSettingGenre not found");
                }
            // 既存セットになかった場合
            } else {
                UserWebMangaSettingGenre item = new UserWebMangaSettingGenre();
                item.setGenre(genreRepository.findById(genreId).get());
                item.setUserWebMangaSetting(setting);
                newList.add(item);
            }

        }

        for (Integer genreId : currentGenreIds) {
            if (!genres.contains(genreId)) {
                Optional<UserWebMangaSettingGenre> OptionalItem = settingGenreRepository.findByUserWebMangaSettingAndGenre(setting, genreRepository.findById(genreId).get());
                if (OptionalItem.isPresent()) {
                    UserWebMangaSettingGenre item = OptionalItem.get();
                    item.setDeleteFlag(true);
                    newList.add(item);
                } else {
                    // ここには到達しないはずですが、安全のため
                    throw new IllegalStateException("Unexpected state: UserWebMangaSettingGenre not found");
                }
            }
        }

        // フラグの更新
        setting.setWebMangaSettingsFreeflag(freeflag);
        setting.setWebMangaSettingsFollowflag(followflag);
        setting.setWebMangaSettingsOneshotflag(oneshotflag);
        setting.setWebMangaSettingsGenres(newList);

        user.setUserWebMangaSetting(setting);
        return userRepository.save(user);
    }






}
