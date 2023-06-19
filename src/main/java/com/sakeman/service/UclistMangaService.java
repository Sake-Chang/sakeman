package com.sakeman.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.UclistManga;
import com.sakeman.repository.UclistMangaRepository;


@Service
public class UclistMangaService {
    private final UclistMangaRepository uclistMangaRepository;

    public UclistMangaService(UclistMangaRepository repository) {
        this.uclistMangaRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<UclistManga> getUclistMangaList() {
        return uclistMangaRepository.findAll();
    }

    /** 1件を検索して返す */
    public UclistManga getUclistManga(Integer id) {
        return uclistMangaRepository.findById(id).get();
    }

    /** uclist_idで検索して返す */
    public List<UclistManga> findByUclistId(Integer uclistId) {
        return uclistMangaRepository.findByUclistId(uclistId);
    }

    /** 登録処理(1件) */
//    @Transactional
    public UclistManga saveUclistManga(UclistManga uclistManga) {
        return uclistMangaRepository.save(uclistManga);
    }

    /** 登録処理(複数件) */
//  @Transactional
  public List<UclistManga> saveUclistMangaAll(List<UclistManga> uclistMangas) {
      return uclistMangaRepository.saveAll(uclistMangas);
  }

}
