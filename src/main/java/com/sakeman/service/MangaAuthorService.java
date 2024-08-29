package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Author;
import com.sakeman.entity.Genre;
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.entity.Tag;
import com.sakeman.repository.MangaAuthorRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class MangaAuthorService {
    private final MangaAuthorRepository mangaAuthorRepository;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<MangaAuthor> getMangaAuthorList() {
        return mangaAuthorRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<MangaAuthor> getListPageable(Pageable pageable){
        return mangaAuthorRepository.findAll(pageable);
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public MangaAuthor getMangaAuthor(Integer id) {
        return mangaAuthorRepository.findById(id).get();
    }

    /** manga_idで検索して返す */
    @Transactional(readOnly = true)
    public List<MangaAuthor> findByMangaId(Integer mangaId) {
        return mangaAuthorRepository.findByMangaId(mangaId);
    }

    /** author_idで検索して返す */
    @Transactional(readOnly = true)
    public List<MangaAuthor> findByAuthorId(Integer authorId) {
        return mangaAuthorRepository.findByAuthorId(authorId);
    }

    /** genreとtagで検索して返す */
    @Transactional(readOnly = true)
    public Optional<MangaAuthor> findByMangaAndAuthor(Manga manga, Author author) {
        return mangaAuthorRepository.findByMangaAndAuthor(manga, author);
    }

    /** 登録処理(1件) */
    @Transactional
    public MangaAuthor saveMangaAuthor(MangaAuthor mangaAuthor) {
        return mangaAuthorRepository.save(mangaAuthor);
    }

    /** 登録処理(複数件) */
    @Transactional
    public List<MangaAuthor> saveMangaAuthorAll(List<MangaAuthor> mangaAuthors) {
        return mangaAuthorRepository.saveAll(mangaAuthors);
    }

    /** 削除
    * @return */
    @Transactional
    public void deleteById(Integer id) {
        mangaAuthorRepository.deleteById(id);
    }

}
