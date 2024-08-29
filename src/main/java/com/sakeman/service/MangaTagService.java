package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.entity.MangaTag;
import com.sakeman.repository.MangaAuthorRepository;
import com.sakeman.repository.MangaTagRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MangaTagService {
    private final MangaTagRepository mangaTagRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<MangaTag> getMangaTagList() {
        return mangaTagRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<MangaTag> getListPageable(Pageable pageable){
        return mangaTagRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public MangaTag getMangaTag(Integer id) {
        return mangaTagRepository.findById(id).get();
    }

    /** manga_idで検索して返す */
    @Transactional(readOnly = true)
    public List<MangaTag> findByMangaId(Integer mangaId) {
        return mangaTagRepository.findByMangaId(mangaId);
    }

    /** tag_idで検索して返す */
    @Transactional(readOnly = true)
    public List<MangaTag> findByTagId(Integer tagId) {
        return mangaTagRepository.findByTagId(tagId);
    }

    /** tag_name,user_id,manga_idで検索して返す */
    @Transactional(readOnly = true)
    public Optional<MangaTag> findByTagTagnameAndUserIdAndMangaId(String tagname, Integer userId, Integer mangaId) {
        return mangaTagRepository.findByTagTagnameAndUserIdAndMangaId(tagname, userId, mangaId);
    }

    /** 登録処理(1件) */
    @Transactional
    public MangaTag saveMangaTag(MangaTag mangaTag) {
        return mangaTagRepository.save(mangaTag);
    }

    /** 登録処理(複数件) */
    @Transactional
    public List<MangaTag> saveMangaTagAll(List<MangaTag> mangaTags) {
        return mangaTagRepository.saveAll(mangaTags);
    }

}
