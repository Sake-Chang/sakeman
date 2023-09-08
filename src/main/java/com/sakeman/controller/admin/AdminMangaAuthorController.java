package com.sakeman.controller.admin;

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

import com.sakeman.entity.Manga;
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
    public String getList(Model model, @PageableDefault(page=0, size=100, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("pages", service.getListPageable(pageable));
        model.addAttribute("mangaauthorlist", service.getListPageable(pageable).getContent());
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

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("mangaAuthor", service.getMangaAuthor(id));
        return "admin/manga-author/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateMangaAuthor(@PathVariable Integer id, @ModelAttribute MangaAuthor mangaAuthor, Model model) {
        service.saveMangaAuthor(mangaAuthor);
        return "redirect:/admin/manga-author/list";
    }


}
