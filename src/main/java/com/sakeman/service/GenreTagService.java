package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Genre;
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.repository.GenreTagRepository;
import com.sakeman.repository.UclistMangaRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class GenreTagService {
    private final GenreTagRepository repository;

    /** 全件を検索して返す **/
    public List<GenreTag> getGenreTagList() {
        return repository.findAll();
    }

    /** 1件を検索して返す */
    public GenreTag getGenreTag(Integer id) {
        return repository.findById(id).get();
    }

    /** uclist_idで検索して返す */
    public List<GenreTag> findByTagId(Integer tagId) {
        return repository.findByTagId(tagId);
    }

    /** uclist_idで検索して返す */
    public List<GenreTag> findByGenreId(Integer genreId) {
        return repository.findByGenreId(genreId);
    }

    /** genreとtagで検索して返す */
    public Optional<GenreTag> findByGenreAndTag(Genre genre, Tag tag) {
        return repository.findByGenreAndTag(genre, tag);
    }

    /** 登録処理(1件) */
//    @Transactional
    public GenreTag saveGenreTag(GenreTag genreTag) {
        return repository.save(genreTag);
    }

    /** 登録処理(複数件) */
//  @Transactional
  public List<GenreTag> saveGenreTagAll(List<GenreTag> genreTags) {
      return repository.saveAll(genreTags);
  }

  /** 削除
 * @return */
  @Transactional
  public void deleteById(Integer id) {
      repository.deleteById(id);
  }

}
