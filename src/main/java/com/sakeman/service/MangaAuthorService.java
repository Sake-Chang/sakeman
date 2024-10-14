package com.sakeman.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private final AuthorService authService;

    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<MangaAuthor> getMangaAuthorList() {
        return mangaAuthorRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<MangaAuthor> getListPageable(Pageable pageable){
        return mangaAuthorRepository.findAllActive(pageable);
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

    public List<MangaAuthor> getMalistToAdd(Set<String> inputAuthorNames, Manga manga) {
        List<MangaAuthor> malistToAdd = new ArrayList<>();
        for (String authorName : inputAuthorNames) {
            List<Author> existingAuthors = authService.findByName(authorName);

            Author thisAuthor;
            if (existingAuthors.isEmpty()) {
                thisAuthor = new Author();
                thisAuthor.setName(authorName);
                thisAuthor.setDeleteFlag(0);
                authService.saveAuthor(thisAuthor);
            } else {
                thisAuthor = existingAuthors.get(0);
            }

            MangaAuthor newMangaAuthor = new MangaAuthor();
            newMangaAuthor.setManga(manga);
            newMangaAuthor.setAuthor(thisAuthor);
            malistToAdd.add(newMangaAuthor);
        }

        return malistToAdd;
    }

    public List<MangaAuthor> getMalistToRemove(List<MangaAuthor> currentMangaAuthors, Set<String> inputAuthorNames) {
        List<MangaAuthor> malistToRemove = new ArrayList<>();
        for (MangaAuthor mangaAuthor : currentMangaAuthors) {
            if (!inputAuthorNames.contains(mangaAuthor.getAuthor().getName())) {
                malistToRemove.add(mangaAuthor);
            } else {
                inputAuthorNames.remove(mangaAuthor.getAuthor().getName());
            }
        }
        return malistToRemove;
    }

    /** 削除 */
    @Transactional
    public void deleteById(Integer id) {
        mangaAuthorRepository.deleteById(id);
    }

    @Transactional
    public void deleteAll(List<MangaAuthor> authorsToRemove) {
        if (!authorsToRemove.isEmpty()) {
            mangaAuthorRepository.deleteAll(authorsToRemove); // JPAのdeleteAllを呼び出す
        }
    }


}
