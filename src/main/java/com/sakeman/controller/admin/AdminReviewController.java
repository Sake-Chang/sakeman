package com.sakeman.controller.admin;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.Review;
import com.sakeman.service.ReviewService;
import com.sakeman.service.UserDetail;



@Controller
@RequestMapping("admin/review")
public class AdminReviewController {
    private final ReviewService service;

    public AdminReviewController(ReviewService service) {
        this.service = service;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model) {
        model.addAttribute("reviewlist", service.getReviewList());
        return "admin/review/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("review", service.getReview(id));
        return "admin/review/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Review review, Model model) {
        return "admin/review/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Review review, @AuthenticationPrincipal UserDetail userDetail, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(review, model);
        }
        review.setUser(userDetail.getUser());
        review.setDeleteFlag(0);
        service.saveReview(review);
        return "redirect:/admin/review/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("review", service.getReview(id));
        return "admin/review/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateReview(@PathVariable Integer id, @ModelAttribute Review review, Model model) {
        /** 変更前の状態を取得 */
        Review rvw = service.getReview(id);

        review.setId(rvw.getId());

        service.saveReview(review);

        return "redirect:/admin/review/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteReview(@PathVariable("id") Integer id, @ModelAttribute Review review, Model model) {
        Review rvw = service.getReview(id);
        rvw.setDeleteFlag(1);
        service.saveReview(rvw);
        return "redirect:/admin/review/list";
    }
}