package com.sakeman.controller.admin;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Manga;
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
    public String getList(Model model) {
        model.addAttribute("uclistlist", service.getUclistList());
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

}
