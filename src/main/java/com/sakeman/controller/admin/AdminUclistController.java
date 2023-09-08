package com.sakeman.controller.admin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.service.MangaService;
import com.sakeman.service.UclistMangaService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;


@Controller
@RequestMapping("admin/uclist")
public class AdminUclistController {

    private final UclistService service;
    private final MangaService mangaService;
    private final UclistMangaService umService;

    public AdminUclistController(UclistService service, MangaService mangaService, UclistMangaService umService) {
        this.service = service;
        this.mangaService = mangaService;
        this.umService = umService;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("pages", service.getUclistListPageable(pageable));
        model.addAttribute("uclistlist", service.getUclistListPageable(pageable).getContent());
        return "admin/uclist/list";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Uclist uclist, Model model) {
        List<Manga> mangalist =mangaService.getMangaList();
        model.addAttribute("mangalist", mangalist);
        return "admin/uclist/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Uclist uclist, @AuthenticationPrincipal UserDetail userDetail, @RequestParam("mangaId") List<Integer> mangaIds, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(uclist, model);
        }
        uclist.setDeleteFlag(0);
        uclist.setUser(userDetail.getUser());
        service.saveUclist(uclist);

        for(Integer i=0; i < mangaIds.size(); i++) {
            Integer mangaId = mangaIds.get(i);
            UclistManga um = new UclistManga();
            um.setManga(mangaService.getManga(mangaId));
            um.setUclist(uclist);
            um.setDeleteFlag(0);
            umService.saveUclistManga(um);
        }

        return "redirect:/admin/uclist/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("uclist", service.getUclist(id));
        return "admin/uclist/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}")
    public String updateReview(@PathVariable Integer id, @ModelAttribute Uclist uclist, @RequestParam("mangaId") List<Integer> mangaIds, Model model) {
        /** 変更前の状態を取得 */
        Uclist ucl = service.getUclist(id);
        uclist.setId(ucl.getId());
        service.saveUclist(uclist);

        // リストが持っているUclistMangaを取得
        List<UclistManga> uclistMangas = umService.findByUclistId(ucl.getId());
        // 空のリストを用意（含まれるmangaIdのリスト）
        List<Integer> mangalist = new ArrayList<>();
        // リストが持っているUclistMangaを回しながらmangaIdをmangalistに追加
        for(int i=0; i<uclistMangas.size(); i++) {
            Integer mangaId = uclistMangas.get(i).getManga().getId();
            mangalist.add(mangaId);
        }
        List<Integer> ml = new ArrayList<>(new HashSet<>(mangalist));

        // 既存のリスト(ml)に含まれるかをチェックしてなければ登録
        for(int j=0; j < mangaIds.size(); j++) {
            Integer mangaId = mangaIds.get(j);
            if (!ml.contains(mangaId)) {
                UclistManga um = new UclistManga();
                um.setManga(mangaService.getManga(mangaId));
                um.setUclist(uclist);
                um.setDeleteFlag(0);
                umService.saveUclistManga(um);
            }
        }
        for(int k=0; k<ml.size(); k++) {
            Integer mangaId2 = ml.get(k);
            if (!mangaIds.contains(mangaId2)) {
                Integer deleteId = umService.findByUclistAndManga(service.getUclist(id), mangaService.getManga(mangaId2)).get().getId();
                umService.deleteById(deleteId);
            }
        }

        return "redirect:/admin/uclist/list";
    }

    /** 削除処理 */
//    @GetMapping("delete/{id}/")
//    public String deleteReview(@PathVariable("id") Integer id, @ModelAttribute Review review, Model model) {
//        Review rvw = service.getReview(id);
//        rvw.setDeleteFlag(1);
//        service.saveReview(rvw);
//        return "redirect:/admin/review/list";
//    }

}
