package com.sakeman.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.repository.MangaRepository;
import com.sakeman.repository.ReviewRepository;
import com.sakeman.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final MangaRepository mangaRepository;


    /** 全件を検索して返す **/
    @Transactional(readOnly = true)
    public List<Review> getReviewList() {
        return reviewRepository.findAll();
    }

    /** ページネーション */
    @Transactional(readOnly = true)
    public Page<Review> getReviewListPageable(Pageable pageable){
        return reviewRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> findByTitleIsNotNullOrContentIsNotNull(Pageable pageable){
        return reviewRepository.findByTitleIsNotNullOrContentIsNotNull(pageable);
    }


    /** 1件を検索して返す */
    @Transactional(readOnly = true)
    public Review getReview(Integer id) {
        return reviewRepository.findById(id).get();
    }

    @Transactional(readOnly = true)
    public List<Review> getTop10() {
        return reviewRepository.findTop10ByOrderByRegisteredAtDesc();
    }

    /** マンガIDで検索して返す */
    @Transactional(readOnly = true)
    @Cacheable(value = "review", key = "'allEntries'")
    public List<Review> getReviewByMangaId(Integer id) {
        return reviewRepository.findByMangaId(id);
    }

    /** マンガIDで検索して返す(Pageable) */
    @Transactional(readOnly = true)
    public Page<Review> getReviewByMangaIdPageable(Integer id, Pageable pageable) {
        return reviewRepository.findByMangaId(id, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Review> findByMangaIdAndTitleIsNotNullOrContentIsNotNull(Integer id, Pageable pageable) {
        return reviewRepository.findByMangaIdAndTitleIsNotNullOrContentIsNotNull(id, pageable);
    }

    /** 著者で検索 **/
    @Transactional(readOnly = true)
    public Page<Review> getDistinctByMangaMangaAuthorsAuthorId(Integer aId, Pageable pageable) {
        return reviewRepository.findDistinctByMangaMangaAuthorsAuthorId(aId, pageable);
    }

    /** ユーザーIDとまんがIDで検索 **/
    @Transactional(readOnly = true)
    public Optional<Review> getReviewByUserIdAndMangaId(Integer uid, Integer mid) {
        return reviewRepository.findByUserIdAndMangaId(uid, mid);
    }

    @Transactional
    public Review updateOrCreateReview(Integer userId, Integer mangaId, Integer rating) {
        Optional<Review> optionalReview = reviewRepository.findByUserIdAndMangaId(userId, mangaId);

        Review review = optionalReview.orElse(new Review());
        if (!optionalReview.isPresent()) {
            review.setUser(userRepository.findById(userId));
            review.setManga(mangaRepository.findById(mangaId).orElseThrow(() -> new RuntimeException("Manga not found")));
        }

        review.setRating(rating);

        return saveReview(review);
    }


    /** 登録処理 */
    @Transactional
    @CacheEvict(value = {"review"}, allEntries = true)
    public Review saveReview (Review review) {
        return reviewRepository.save(review);
    }

}
