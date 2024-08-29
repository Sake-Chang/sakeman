package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.repository.UclistMangaRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UclistMangaService {
    private final UclistMangaRepository uclistMangaRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<UclistManga> getUclistMangaList() {
        return uclistMangaRepository.findAll();
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public UclistManga getUclistManga(Integer id) {
        return uclistMangaRepository.findById(id).get();
    }

    /** uclist_idで検索して返す */
    @Transactional(readOnly = true)
    public List<UclistManga> findByUclistId(Integer uclistId) {
        return uclistMangaRepository.findByUclistId(uclistId);
    }

    /** uclistとmangaで検索して返す */
    @Transactional(readOnly = true)
    public Optional<UclistManga> findByUclistAndManga(Uclist uclist, Manga manga) {
        return uclistMangaRepository.findByUclistAndManga(uclist, manga);
    }

    /** 登録処理(1件) */
    @Transactional
    public UclistManga saveUclistManga(UclistManga uclistManga) {
        return uclistMangaRepository.save(uclistManga);
    }

    /** 登録処理(複数件) */
    @Transactional
    public List<UclistManga> saveUclistMangaAll(List<UclistManga> uclistMangas) {
        return uclistMangaRepository.saveAll(uclistMangas);
    }

    /** 削除
    * @return */
    @Transactional
    public void deleteById(Integer id) {
        uclistMangaRepository.deleteById(id);
    }

}
