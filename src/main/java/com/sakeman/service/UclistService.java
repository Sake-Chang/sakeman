package com.sakeman.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Uclist;
import com.sakeman.repository.UclistRepository;


@Service
@Transactional
public class UclistService {
    private final UclistRepository uclistRepository;

    public UclistService(UclistRepository repository) {
        this.uclistRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<Uclist> getUclistList() {
        return uclistRepository.findAll();
    }

    /** ページネーション */
    public Page<Uclist> getUclistListPageable(Pageable pageable){
        return uclistRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    public Uclist getUclist(Integer id) {
        return uclistRepository.findById(id).get();
    }

    /** マンガで検索 */
    public List<Uclist> getByManga(Manga manga) {
        return uclistRepository.findByUclistMangasManga(manga);
    }

    /** マンガで検索(Pageable) */
    public Page<Uclist> getByMangaPageable(Manga manga, Pageable pageable) {
        return uclistRepository.findByUclistMangasManga(manga, pageable);
    }

    /** マンガIDで検索(Pageable) */
    public Page<Uclist> getByMangaIdPageable(Integer mangaId, Pageable pageable) {
        return uclistRepository.findByUclistMangasMangaId(mangaId, pageable);
    }

    /** 著者IDで検索 **/
    public Page<Uclist> gettDistinctByUclistMangasMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable) {
        return uclistRepository.findDistinctByUclistMangasMangaMangaAuthorsAuthorId(aId, pageable);
    }

    /** 登録処理 */
    @Transactional
    public Uclist saveUclist (Uclist uclist) {
        return uclistRepository.save(uclist);
    }

}
