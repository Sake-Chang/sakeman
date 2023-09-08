package com.sakeman.controller.admin;

import java.util.List;

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

import com.sakeman.entity.User;
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaUpdateInfoService;



@Controller
@RequestMapping("admin/web-manga-media")
public class AdminWebMangaMediaController {
    private final WebMangaUpdateInfoService infoService;
    private final WebMangaMediaService mediaService;

    public AdminWebMangaMediaController(WebMangaUpdateInfoService infoService, WebMangaMediaService mediaService) {
        this.infoService = infoService;
        this.mediaService = mediaService;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model) {
        model.addAttribute("medialist", mediaService.getWebMangaMediaList());
        return "admin/web-manga-media/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("media", mediaService.getWebMangaMedia(id));
        return "admin/web-manga-media/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute WebMangaMedia webMangaMedia, Model model) {
        return "admin/web-manga-media/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated WebMangaMedia webMangaMedia, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(webMangaMedia, model);
        }
        /** 作品を保存 */
        mediaService.saveWebMangaMedia(webMangaMedia);

        return "redirect:/admin/web-manga-media/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("media", mediaService.getWebMangaMedia(id));
        return "admin/web-manga-media/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateWebMangaMedia(@PathVariable Integer id, @ModelAttribute WebMangaMedia media, Model model) {
        mediaService.saveWebMangaMedia(media);
        return "redirect:/admin/web-manga-media/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteManga(@PathVariable("id") Integer id, @ModelAttribute WebMangaMedia media, Model model) {
        WebMangaMedia webMangaMedia = mediaService.getWebMangaMedia(id);
        webMangaMedia.setDeleteFlag(1);
        mediaService.saveWebMangaMedia(webMangaMedia);
        return "redirect:/admin/web-manga-media/list";
    }
}