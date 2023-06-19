package com.sakeman.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.MangaAuthor;
import com.sakeman.service.MangaAuthorService;


@Controller
@RequestMapping("admin/manga-author")
public class AdminMangaAuthorController {

    private final MangaAuthorService service;

    public AdminMangaAuthorController(MangaAuthorService service) {
        this.service = service;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model) {
        model.addAttribute("mangaauthorlist", service.getMangaAuthorList());
        return "admin/manga-author/list";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute MangaAuthor mangaAuthor, Model model) {
        return "admin/manga-author/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated MangaAuthor mangaAuthor, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(mangaAuthor, model);
        }
        service.saveMangaAuthor(mangaAuthor);
        return "redirect:/admin/manga-author/list";
    }


}
