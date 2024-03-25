package com.sakeman.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

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
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.MangaService;
import com.sakeman.service.StringUtilService;

import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
@RequestMapping("admin/manga")
public class AdminMangaController {
    private final MangaService service;
    private final AuthorService authService;
    private final MangaAuthorService maService;
    private final StringUtilService utilService;

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=100, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getMangaListPageable(pageable));
        model.addAttribute("mangalist", service.getMangaListPageable(pageable).getContent());
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
    @Transactional
    public String postRegister(@Validated Manga manga, @RequestParam("authorName") List<String> authorNames, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(manga, model);
        }
        /** 作品を保存 */
        manga.setTitleCleanse(utilService.cleanse(manga.getTitle()));
        manga.setDeleteFlag(0);
        manga.setDisplayFlag(1);
        service.saveManga(manga);

        /** mangaAuthorを作成＆保存 */
        for(Integer i=0; i < authorNames.size(); i++) {
            String authorName = authorNames.get(i);
            List<Author> thisAuthors = authService.findByName(authorName);

            MangaAuthor ma = new MangaAuthor();
            ma.setManga(manga);
            if (thisAuthors.size() != 0) {
                ma.setAuthor(thisAuthors.get(0));
                maService.saveMangaAuthor(ma);
            } else {
                Author author = new Author();
                author.setName(authorName);
                author.setDeleteFlag(0);
                authService.saveAuthor(author);

                ma.setAuthor(author);
                maService.saveMangaAuthor(ma);
            }
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
    public String updateManga(@PathVariable Integer id, @ModelAttribute Manga manga, @RequestParam("authorId") List<Integer> authorIds, Model model) {
        manga.setTitleCleanse(utilService.cleanse(manga.getTitle()));
        service.saveManga(manga);

        // 既存のmangaAuthorを取得
        List<MangaAuthor> mangaAuthors = maService.findByMangaId(id);
        // 空のリストを用意（含まれるauthorIdのリスト）
        List<Integer> authorlist = new ArrayList<>();
        // 既存のMangaAuthorを回しながらauthorIdをauthorIdsに追加
        for(int i=0; i<mangaAuthors.size(); i++) {
            Integer authorId = mangaAuthors.get(i).getAuthor().getId();
            authorlist.add(authorId);
        }
        List<Integer> aids = new ArrayList<>(new HashSet<>(authorlist));

        // 既存のリスト(ml)に含まれるかをチェックしてなければ登録
        for(int j=0; j < authorIds.size(); j++) {
            Integer authorId = authorIds.get(j);
            if (!aids.contains(authorId)) {
                MangaAuthor ma = new MangaAuthor();
                ma.setAuthor(authService.getAuthor(authorId));
                ma.setManga(manga);
                maService.saveMangaAuthor(ma);
            }
        }
        for(int k=0; k<aids.size(); k++) {
            Integer authorId2 = aids.get(k);
            if (!authorIds.contains(authorId2)) {
                Integer deleteId = maService.findByMangaAndAuthor(service.getManga(id), authService.getAuthor(authorId2)).get().getId();
                maService.deleteById(deleteId);
            }
        }
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