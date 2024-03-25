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
@RequestMapping("admin/tag")
public class AdminTagController {
    private final TagService service;
    private final GenreService genreService;
    private final GenreTagService gtService;

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=50, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getTagListPageable(pageable));
        model.addAttribute("taglist", service.getTagListPageable(pageable).getContent());
        return "admin/tag/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("tag", service.getTag(id));
        return "admin/tag/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Tag tag, Model model) {
        return "admin/tag/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Tag tag, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(tag, model);
        }
        service.saveTag(tag);
        return "redirect:/admin/tag/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("tag", service.getTag(id));
        return "admin/tag/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateTag(@PathVariable Integer id, @ModelAttribute Tag tag, @RequestParam("genreId") List<Integer> genreIds, Model model) {
        /** 変更前の状態を取得 */
        tag.setId(id);
        service.saveTag(tag);

        // 既存のgenreTagを取得
        List<GenreTag> genreTags = gtService.findByTagId(id);
        // 空のリストを用意（含まれるgenreIdのリスト）
        List<Integer> genrelist = new ArrayList<>();
        // 既存のGenreTagを回しながらgenreIdをgenreIdsに追加
        for(int i=0; i<genreTags.size(); i++) {
            Integer genreId = genreTags.get(i).getGenre().getId();
            genrelist.add(genreId);
        }
        List<Integer> gids = new ArrayList<>(new HashSet<>(genrelist));

        // 既存のリスト(ml)に含まれるかをチェックしてなければ登録
        for(int j=0; j < genreIds.size(); j++) {
            Integer genreId = genreIds.get(j);
            if (!gids.contains(genreId)) {
                GenreTag gt = new GenreTag();
                gt.setGenre(genreService.getGenre(genreId));
                gt.setTag(tag);
                gtService.saveGenreTag(gt);
            }
        }
        for(int k=0; k<gids.size(); k++) {
            Integer genreId2 = gids.get(k);
            if (!genreIds.contains(genreId2)) {
                Integer deleteId = gtService.findByGenreAndTag(genreService.getGenre(genreId2), service.getTag(id)).get().getId();
                gtService.deleteById(deleteId);
            }
        }

        return "redirect:/admin/tag/list";
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