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

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.MangaService;



@Controller
@RequestMapping("admin/manga")
public class AdminMangaController {
    private final MangaService service;
    private final AuthorService authService;
    private final MangaAuthorService maService;

    public AdminMangaController(MangaService service, AuthorService authService, MangaAuthorService maService) {
        this.service = service;
        this.authService = authService;
        this.maService = maService;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model) {
        model.addAttribute("mangalist", service.getMangaList());
        return "admin/manga/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("manga", service.getManga(id));
        return "admin/manga/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Manga manga, Model model) {
        List<Author> authorlist = authService.getAuthorList();
        model.addAttribute("authorlist", authorlist);
        return "admin/manga/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Manga manga, @RequestParam("authorId") List<Integer> authorIds, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(manga, model);
        }
        /** 作品を保存 */
        manga.setDeleteFlag(0);
        manga.setDisplayFlag(1);
        service.saveManga(manga);

        /** mangaAuthorを作成＆保存 */
        for(Integer i=0; i < authorIds.size(); i++) {
            Integer authorId = authorIds.get(i);
            MangaAuthor ma = new MangaAuthor();
            ma.setAuthor(authService.getAuthor(authorId));
            ma.setManga(manga);
            maService.saveMangaAuthor(ma);
        }

        return "redirect:/admin/manga/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("manga", service.getManga(id));
        return "admin/manga/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateManga(@PathVariable Integer id, @ModelAttribute Manga manga, Model model) {
        service.saveManga(manga);
        return "redirect:/admin/manga/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteManga(@PathVariable("id") Integer id, @ModelAttribute Manga manga, Model model) {
        Manga mng = service.getManga(id);
        mng.setDeleteFlag(1);
        service.saveManga(mng);
        return "redirect:/admin/manga/list";
    }
}