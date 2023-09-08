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
import com.sakeman.entity.WebMangaTitleConverter;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.repository.LikeRepository;
import com.sakeman.repository.WebMangaTitleConverterRepository;


@Service
public class WebMangaTitleConverterService {

    private final WebMangaTitleConverterRepository repository;

    public WebMangaTitleConverterService(WebMangaTitleConverterRepository repository) {
        this.repository = repository;
    }

    /** 全件を検索して返す **/
    public List<WebMangaTitleConverter> getConverterList() {
        return repository.findAll();
    }

    /** 1件を検索して返す */
    public WebMangaTitleConverter getWebMangaTitleConverter(Integer id) {
        return repository.findById(id).get();
    }

    /** titleStringとauthorStringで検索 */
    public Optional<WebMangaTitleConverter> findByTitleStrignAndAuthorString(String titleString, String authorString){
        return repository.findByTitleStringAndAuthorString(titleString, authorString);
    }

    /** 登録処理 */
    @Transactional
    public WebMangaTitleConverter saveWebMangaTitleConverter(WebMangaTitleConverter wmtc) {
        return repository.save(wmtc);
    }

    /** 削除処理 */
    @Transactional
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

}
