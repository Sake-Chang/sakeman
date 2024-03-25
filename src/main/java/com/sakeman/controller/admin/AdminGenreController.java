package com.sakeman.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

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
import com.sakeman.entity.Genre;
import com.sakeman.entity.GenreTag;
import com.sakeman.entity.Tag;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.service.AuthorFollowService;
import com.sakeman.service.AuthorService;
import com.sakeman.service.GenreService;
import com.sakeman.service.GenreTagService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.TagService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("admin/genre")
public class AdminGenreController {
    private final GenreService service;
    private final GenreTagService gtService;
    private final TagService tagService;

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=50, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getGenreListPageable(pageable));
        model.addAttribute("genrelist", service.getGenreListPageable(pageable).getContent());
        return "admin/genre/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("genre", service.getGenre(id));
        return "admin/genre/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Genre genre, Model model) {
        return "admin/genre/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Genre genre, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(genre, model);
        }
        service.saveGenre(genre);
        return "redirect:/admin/genre/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("genre", service.getGenre(id));
        return "admin/genre/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateGenre(@PathVariable Integer id, @ModelAttribute Genre genre, @RequestParam("tagId") List<Integer> tagIds, Model model) {
        /** 変更前の状態を取得 */
        genre.setId(id);
        service.saveGenre(genre);

        // 既存のgenreTagを取得
        List<GenreTag> genreTags = gtService.findByGenreId(id);
        // 空のリストを用意（含まれるtagIdのリスト）
        List<Integer> taglist = new ArrayList<>();
        // 既存のGenreTagを回しながらtagIdをtagIdsに追加
        for(int i=0; i<genreTags.size(); i++) {
            Integer tagId = genreTags.get(i).getTag().getId();
            taglist.add(tagId);
        }
        List<Integer> tids = new ArrayList<>(new HashSet<>(taglist));

        // 既存のリスト(ml)に含まれるかをチェックしてなければ登録
        for(int j=0; j < tagIds.size(); j++) {
            Integer tagId = tagIds.get(j);
            if (!tids.contains(tagId)) {
                GenreTag gt = new GenreTag();
                gt.setTag(tagService.getTag(tagId));
                gt.setGenre(genre);
                gtService.saveGenreTag(gt);
            }
        }
        for(int k=0; k<tids.size(); k++) {
            Integer tagId2 = tids.get(k);
            if (!tagIds.contains(tagId2)) {
                Integer deleteId = gtService.findByGenreAndTag(service.getGenre(id), tagService.getTag(tagId2)).get().getId();
                gtService.deleteById(deleteId);
            }
        }

        return "redirect:/admin/genre/list";
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