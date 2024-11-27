package com.sakeman.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.sakeman.dto.MangaAdminResponseDTO;
import com.sakeman.dto.MangaForShelfDTO;
import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.entity.ReadStatus;
import com.sakeman.entity.Review;
import com.sakeman.exception.ResourceNotFoundException;
import com.sakeman.repository.MangaRepository;
import com.sakeman.repository.MangaSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MangaService {
    private final MangaRepository mangaRepository;
    private final StringUtilService utilService;


    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Manga> getMangaList() {
        return mangaRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Manga> getMangaListPageable(Pageable pageable){
        return mangaRepository.findAll(pageable);
    }

    /** 検索結果 */
    @Transactional(readOnly = true)
    public Page<Manga> getSearchResult(Manga manga, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return mangaRepository.findAll(Example.of(manga, matcher), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Manga> searchManga(String[] keywords, Pageable pageable) {
        Page<Manga> resultPage = mangaRepository.findAll(MangaSpecifications.searchManga(keywords), pageable);
        for (Manga manga : resultPage.getContent()) {
            System.out.println(manga.getTitle());
        }

        return resultPage;
    }

    /** 検索結果 (select2用) */
    @Transactional(readOnly = true)
    public List<Manga> getSearchResult(Manga manga) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方
        return mangaRepository.findAll(Example.of(manga, matcher));
    }

    @Transactional(readOnly = true)
    public Page<Manga> getSearchResultWithPaging(Manga manga, Pageable pageable) {
        ExampleMatcher matcher = ExampleMatcher
                .matching() // and条件
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // Like句
                .withIgnoreCase(); // 大文字小文字の両方

        return mangaRepository.findAll(Example.of(manga, matcher), pageable);
    }

    /** Like検索結果 */
    @Transactional(readOnly = true)
    public List<Manga> getLikeSearch(String string) {
        return mangaRepository.findByTitleLike("%" + string + "%");
    }

    @Transactional(readOnly = true)
    public List<Manga> getByIdBetween(int start, int end) {
        return mangaRepository.findByIdBetween(start, end);
    }

    /** クレンズで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByTitleCleanse(String titleCleanse) {
        return mangaRepository.findByTitleCleanse(titleCleanse);
    }

    @Transactional(readOnly = true)
    public long countAll() {
        return mangaRepository.count();
    }

    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Manga getManga(Integer id) {
        return mangaRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public Manga getMangaOrThrow(Integer id) {
        return mangaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manga not found with id " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Manga> getMangaByTitle(String title) {
        return mangaRepository.findByTitle(title);
    }

    /** 著者IDで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByAuthorId(Integer userId) {
        return mangaRepository.findByMangaAuthorsAuthorId(userId);
    }

    /** 著者IDで検索Pageable */
    @Transactional(readOnly = true)
    public Page<Manga> getMangaByAuthorId(Integer userId, Pageable pageable) {
        return mangaRepository.findByMangaAuthorsAuthorId(userId, pageable);
    }

    /** 著者リストで検索 */
    @Transactional(readOnly = true)
    public List<Manga> getMangaByAuthorsIn(List<Author> authors) {
        return mangaRepository.findByMangaAuthorsAuthorIn(authors);
    }

    /** 著者リストで検索 Distinct */
    @Transactional(readOnly = true)
    public Page<Manga> getDistinctMangaByAuthorsIn(List<Author> authors, Pageable pageable) {
        return mangaRepository.findDistinctByMangaAuthorsAuthorIn(authors, pageable);
    }

    /** Admin用検索結果 */
    public Page<Manga> searchMangas(String searchValue, Pageable pageable) {
        Page<Manga> pageData;
        if (StringUtils.hasText(searchValue)) {
            Manga manga = new Manga();
            manga.setTitle(searchValue);
            pageData = getSearchResult(manga, pageable);
        } else {
            pageData = getMangaListPageable(pageable);
        }
        return pageData;
    }

    public List<MangaAdminResponseDTO> getResponseData(Page<Manga> pageData) {
        return pageData.getContent().stream().map(m -> {
            List<String> authorNames = m.getMangaAuthors().stream()
                .map(ma -> ma.getAuthor().getName())
                .collect(Collectors.toList());

            // DTOをコンストラクタで初期化
            return new MangaAdminResponseDTO(
                m.getId(),
                m.getTitle(),
                m.getVolume(),
                m.getPublisher(),
                m.getPublishedIn(),
                String.join(", ", authorNames)
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Manga> getMangaByUserIdAndReadStatus(Integer userId, ReadStatus.Status status, Pageable pageable){
        return mangaRepository.findByReadStatusUserIdAndReadStatusStatus(userId, status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Manga> getMangaByUserIdAndReadStatusSortByRating(Integer userId, ReadStatus.Status status, Pageable pageable){
        return mangaRepository.findByReadStatusUserIdAndReadStatusStatusSortByRating(userId, status, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Manga> getMangaByWebMangaFollowsUserId(Integer userId, Pageable pageable){
        return mangaRepository.findByWebMangaFollowsUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Manga> getMangaByWebMangaFollowsUserIdSortByRating(Integer userId, Pageable pageable){
        return mangaRepository.findByWebMangaFollowsUserIdSortByRating(userId, pageable);
    }

    public Page<MangaForShelfDTO> convertToMangaForShelfDTO(Page<Manga> mangas, Integer userId) {
        return mangas.map(manga -> mapToDto(manga, userId));
    }

    private MangaForShelfDTO mapToDto(Manga manga, Integer userId) {
        return new MangaForShelfDTO(
                manga.getId(),
                manga.getTitle(),
                manga.getCalligraphy(),
                manga.getCompletionFlag(),
                manga.getVolume(),
                getUserRatingForManga(manga, userId),
                areTitleAndContentNull(manga, userId),
                manga.getReviews().size(),
                calculateAverageRating(manga),
                manga.getMangaAuthors().stream()
                     .map(MangaAuthor::getAuthor)
                     .toList()
        );
    }

    private Integer getUserRatingForManga(Manga manga, Integer userId) {
        return manga.getReviews().stream()
                .filter(review -> review.getUser().getId().equals(userId))
                .map(Review::getRating)
                .findFirst()
                .orElse(null);
    }

    private boolean areTitleAndContentNull(Manga manga, Integer userId) {
        return manga.getReviews().stream()
                .filter(review -> review.getUser().getId().equals(userId))
                .anyMatch(review -> review.getTitle() != null || review.getContent() != null);
    }

    private double calculateAverageRating(Manga manga) {
        double average = manga.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        return Math.round(average * 100.0) / 100.0;
    }


    @Transactional
    public void setMangaDetailAndSave(Manga inputData, List<MangaAuthor> malistToAdd) {
        inputData.setTitleCleanse(utilService.cleanse(inputData.getTitle()));
        inputData.setDeleteFlag(0);
        inputData.setDisplayFlag(1);
        inputData.setMangaAuthors(malistToAdd);
        saveManga(inputData);
    }

    @Transactional
    public void updateMangaDetail(Manga existingManga, Manga inputData, List<MangaAuthor> currentMangaAuthors) {
        existingManga.setTitle(inputData.getTitle());
        existingManga.setVolume(inputData.getVolume());
        existingManga.setPublisher(inputData.getPublisher());
        existingManga.setPublishedIn(inputData.getPublishedIn());
        existingManga.setSynopsis(inputData.getSynopsis());
        existingManga.setCalligraphy(inputData.getCalligraphy());
        existingManga.setIsOneShot(inputData.getIsOneShot());
        existingManga.setDeleteFlag(inputData.getDeleteFlag());
        existingManga.setTitleCleanse(utilService.cleanse(inputData.getTitle()));
        existingManga.setMangaAuthors(currentMangaAuthors);

        saveManga(existingManga);
    }

    @Transactional
    public void titleCleanse(int start, int end) {
        List<Manga> mangalist = getByIdBetween(start, end);
        int num = mangalist.size();
        List<Manga> mangas = new ArrayList<>();
        for (int j=0; j<num; j++) {
            Manga manga = mangalist.get(j);
            String titleCleanse = utilService.cleanse(manga.getTitle());
            manga.setTitleCleanse(titleCleanse);
            mangas.add(manga);
        }
        saveAllManga(mangas);
    }


    /** 登録処理 */
    @Transactional
    public Manga saveManga (Manga manga) {
        return mangaRepository.save(manga);
    }

    @Transactional
    public List<Manga> saveAllManga(List<Manga> mangalist) {
        return mangaRepository.saveAll(mangalist);
    }

}
