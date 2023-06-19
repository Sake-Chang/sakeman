package com.sakeman.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.MangaAuthor;
import com.sakeman.repository.MangaAuthorRepository;


@Service
public class MangaAuthorService {
    private final MangaAuthorRepository mangaAuthorRepository;

    public MangaAuthorService(MangaAuthorRepository repository) {
        this.mangaAuthorRepository = repository;
    }

    /** 全件を検索して返す **/
    public List<MangaAuthor> getMangaAuthorList() {
        return mangaAuthorRepository.findAll();
    }

    /** 1件を検索して返す */
    public MangaAuthor getMangaAuthor(Integer id) {
        return mangaAuthorRepository.findById(id).get();
    }

    /** manga_idで検索して返す */
    public List<MangaAuthor> findByMangaId(Integer mangaId) {
        return mangaAuthorRepository.findByMangaId(mangaId);
    }

    /** 登録処理(1件) */
//    @Transactional
    public MangaAuthor saveMangaAuthor(MangaAuthor mangaAuthor) {
        return mangaAuthorRepository.save(mangaAuthor);
    }

    /** 登録処理(複数件) */
//  @Transactional
  public List<MangaAuthor> saveMangaAuthorAll(List<MangaAuthor> mangaAuthors) {
      return mangaAuthorRepository.saveAll(mangaAuthors);
  }

}
