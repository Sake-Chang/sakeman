package com.sakeman.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.repository.UclistRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UclistService {
    private final UclistRepository uclistRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Uclist> getUclistList() {
        return uclistRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    @Cacheable(value = "uclist", key = "'allEntries'")
    public Page<Uclist> getUclistListPageable(Pageable pageable){
        return uclistRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Uclist getUclist(Integer id) {
        return uclistRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public List<Uclist> getTop10() {
        return uclistRepository.findTop10ByOrderByRegisteredAtDesc();
    }

    /** マンガで検索 */
    @Transactional(readOnly = true)
    public List<Uclist> getByManga(Manga manga) {
        return uclistRepository.findByUclistMangasManga(manga);
    }

    /** マンガで検索(Pageable) */
    @Transactional(readOnly = true)
    public Page<Uclist> getByMangaPageable(Manga manga, Pageable pageable) {
        return uclistRepository.findByUclistMangasManga(manga, pageable);
    }

    /** マンガIDで検索(Pageable) */
    @Transactional(readOnly = true)
    public Page<Uclist> getByMangaIdPageable(Integer mangaId, Pageable pageable) {
        return uclistRepository.findByUclistMangasMangaId(mangaId, pageable);
    }

    /** 著者IDで検索 **/
    @Transactional(readOnly = true)
    public Page<Uclist> gettDistinctByUclistMangasMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable) {
        return uclistRepository.findDistinctByUclistMangasMangaMangaAuthorsAuthorId(aId, pageable);
    }

    /** 登録処理 */
    @Transactional
    @CacheEvict(value = {"uclist"}, allEntries = true)
    public Uclist saveUclist (Uclist uclist) {
        return uclistRepository.save(uclist);
    }

}
