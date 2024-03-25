package com.sakeman.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Badge;
import com.sakeman.service.BadgeService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin/badge")
public class AdminBadgeController {
    private final BadgeService service;

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=50, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        Page<Badge> resultPage = service.getAllPageable(pageable);
        model.addAttribute("pages", resultPage);
        model.addAttribute("badgelist", resultPage.getContent());
        return "admin/badge/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("badge", service.getById(id).get());
        return "admin/badge/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Badge badge, Model model) {
        return "admin/badge/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Badge badge, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(badge, model);
        }
        badge.setDeleteFlag(0);
        service.saveBadge(badge);
        return "redirect:/admin/badge/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("badge", service.getById(id).get());
        return "admin/badge/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateTag(@PathVariable Integer id, @ModelAttribute Badge badge, Model model) {
        /** 変更前の状態を取得 */
        badge.setId(id);
        service.saveBadge(badge);
        return "redirect:/admin/badge/list";
    }

    /** 削除処理 */
//    @GetMapping("delete/{id}/")
//    public String deleteTag(@PathVariable("id") Integer id, @ModelAttribute Tag tag, Model model) {
//        Tag tg = service.getTag(id);
//        tg.setDeleteFlag(1);
//        service.saveAuthor(atr);
//        return "redirect:/admin/author/list";
//    }
}