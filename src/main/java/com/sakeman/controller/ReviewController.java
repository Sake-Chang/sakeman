package com.sakeman.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.service.LikeService;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserFollowService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService service;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final MangaService mangaService;

    /** 一覧表示 (全件) */
    @GetMapping({"/list", "/list/{tab}"})
    public String getList(@AuthenticationPrincipal UserDetail userDetail,
                          @ModelAttribute Manga manga,
                          @PathVariable(name="tab", required=false) String tab,
                          Integer page,
                          Model model) {

        if (tab==null) tab = "recent";
        if (page==null) page = 0;
        Manga mangaobj = null;

        Pageable pageable = getPageable(tab, page);
        Page<Review> reviewlistPage = service.getReviewListPageable(pageable);

        model.addAttribute("reviewlistPages", reviewlistPage);
        model.addAttribute("reviewlist", reviewlistPage.getContent());

        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("mangaobj", mangaobj);

        return "review/list";
        }

    /** 一覧表示 (まんがごと) */
    @GetMapping({"/list-manga/{manga-id}", "/list-manga/{manga-id}/{tab}"})
    public String getListManga(@AuthenticationPrincipal UserDetail userDetail,
                               @ModelAttribute Manga manga,
                               @PathVariable(name="manga-id", required=true) Integer mangaId,
                               @PathVariable(name="tab", required=false) String tab,
                               Integer page,
                               Model model) {

        if (tab==null) tab = "recent";
        if (page==null) page = 0;
        Manga mangaobj = mangaService.getManga(mangaId);

        Pageable pageable = getPageable(tab, page);
        Page<Review> reviewlistPage = service.getReviewByMangaIdPageable(mangaId, pageable);

        model.addAttribute("reviewlistPages", reviewlistPage);
        model.addAttribute("reviewlist", reviewlistPage.getContent());

        /** いいねが多いレビューも返す */
        Pageable pageablePop = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "likes"));
        Page<Review> popReviewPages = service.getReviewListPageable(pageablePop);
        model.addAttribute("popReviewPages", popReviewPages);
        model.addAttribute("popReview", popReviewPages.getContent());

        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("mangaobj", mangaobj);

        return "review/list";
        }

    /** 新規登録（画面表示） */
    @GetMapping("/post")
//    @PreAuthorize("isAuthenticated")
    public String getRegister(@ModelAttribute Review review, Model model) {
//        List<Manga> mangalist = maService.getMangaList();
//        model.addAttribute("mangalist", mangalist);
        return "review/post-review";
    }

    /** 新規登録（画面表示）mangaIdあり */
    @GetMapping("/post/{id}")
//    @PreAuthorize("isAuthenticated")
    public String getRegister(@ModelAttribute Review review, @PathVariable("id") Integer id, Model model) {
//        List<Manga> mangalist = maService.getMangaList();
//        model.addAttribute("mangalist", mangalist);
        Review rev = new Review();
        rev.setManga(mangaService.getManga(id));
        model.addAttribute("review", rev);
        return "review/post-review";
    }

    /** 登録処理 */
    @PostMapping("/post")
    @PreAuthorize("isAuthenticated")
    public String postRegister(@Validated Review review, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, Model model, RedirectAttributes attrs) {
        if(res.hasErrors()) {
            return getRegister(review, model);
        }
        review.setUser(userDetail.getUser());
        review.setDeleteFlag(0);
        service.saveReview(review);

        attrs.addFlashAttribute("success", "レビューが登録されました！");
        return "redirect:/";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("review", service.getReview(id));
        return "review/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateReview(@PathVariable Integer id, @ModelAttribute Review review, Model model) {
        /** 変更前の状態を取得 */
        Review rvw = service.getReview(id);

        review.setId(rvw.getId());

        service.saveReview(review);

        return "redirect:/review/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteReview(@PathVariable("id") Integer id, @ModelAttribute Review review, Model model) {
        Review rvw = service.getReview(id);
        rvw.setDeleteFlag(1);
        service.saveReview(rvw);
        return "redirect:/review/list";
    }

    /** Pageableオブジェクトの取得 */
    public Pageable getPageable(String tab, int page) {
        Pageable pageable;
        if (tab == null) tab = "recent";
        switch (tab) {
            case "recent":
                pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "registeredAt"));
                break;
            case "popular":
                pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "likes"));
                break;
            default:
                pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "registeredAt"));
                break;
        }
        return pageable;
    }
}