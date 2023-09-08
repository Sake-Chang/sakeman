package com.sakeman.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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



@Controller
@RequestMapping("review")
public class ReviewController {
    private final ReviewService service;
    private final LikeService likeService;
    private final ReadStatusService rsService;
    private final UserFollowService ufService;
    private final MangaService maService;

    public ReviewController(ReviewService service, LikeService likeService, ReadStatusService rsService, UserFollowService ufService, MangaService maService) {
        this.service = service;
        this.likeService = likeService;
        this.rsService = rsService;
        this.ufService = ufService;
        this.maService = maService;
    }

    /** 一覧表示（新着順） */
    @GetMapping("")
    @Transactional
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("reviewlist", service.getReviewListPageable(pageable));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));

        return "review/list";
        }

    /** 一覧表示（人気順） */
    @GetMapping("/popular")
    @Transactional
    public String getListPop(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"likes"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("reviewlist", service.getReviewListPageable(pageable));
        model.addAttribute("likelist", likeService.reviewIdListLikedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("followeelist", ufService.followeeIdListFollowedByUser(userDetail));

        return "review/list-popular";
        }

    /** 詳細表示 */
    @GetMapping("/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("review", service.getReview(id));
        return "review/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("/post")
    public String getRegister(@ModelAttribute Review review, Model model) {
//        List<Manga> mangalist = maService.getMangaList();
//        model.addAttribute("mangalist", mangalist);
        return "review/post-review";
    }

    /** 登録処理 */
    @PostMapping("/post")
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
}