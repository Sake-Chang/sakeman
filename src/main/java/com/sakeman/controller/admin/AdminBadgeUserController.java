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

import com.sakeman.entity.Author;
import com.sakeman.entity.BadgeUser;
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Tag;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.service.AuthorFollowService;
import com.sakeman.service.AuthorService;
import com.sakeman.service.BadgeService;
import com.sakeman.service.BadgeUserService;
import com.sakeman.service.GenreService;
import com.sakeman.service.GenreTagService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.TagService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin/badge-user")
public class AdminBadgeUserController {
    private final BadgeUserService service;
    private final BadgeService badgeService;
    private final UserService userService;

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=50, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        Page<BadgeUser> resultPage = service.getAllPageable(pageable);
        model.addAttribute("pages", resultPage);
        model.addAttribute("badgeuserlist", resultPage.getContent());
        return "admin/badge-user/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("badgeuser", service.getById(id).get());
        return "admin/badge-user/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute BadgeUser badgeUser, Model model) {
        model.addAttribute("allBadges", badgeService.getAll());
        model.addAttribute("allUsers", userService.getUserList());

        return "admin/badge-user/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@RequestParam("badge") Integer bId, @RequestParam("user") Integer uId,  Model model) {
//        if(res.hasErrors()) {
//            return getRegister(badgeUser, model);
//        }
        BadgeUser badgeUser = new BadgeUser();
        badgeUser.setBadge(badgeService.getById(bId).get());
        badgeUser.setUser(userService.getUser(uId));
        service.saveBadgeUser(badgeUser);
        return "redirect:/admin/badge-user/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("badgeUser", service.getById(id).get());
        return "admin/badge-user/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateTag(@PathVariable Integer id, @ModelAttribute BadgeUser badgeUser, Model model) {
        /** 変更前の状態を取得 */
        badgeUser.setId(id);
        service.saveBadgeUser(badgeUser);
        return "redirect:/admin/badge-user/list";
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