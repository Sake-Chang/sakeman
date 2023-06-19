package com.sakeman.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.UclistMangaService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;


@Controller
@RequestMapping("uclist")
public class UclistController {

    private final UclistService service;
    private final MangaService mangaService;
    private final UclistMangaService umService;
    private final ReadStatusService rsService;

    public UclistController(UclistService service, MangaService mangaService, UclistMangaService umService, ReadStatusService rsService) {
        this.service = service;
        this.mangaService = mangaService;
        this.umService = umService;
        this.rsService = rsService;
    }

    /** 一覧表示（新着順） */
    @GetMapping("")
    public String getListNew(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"registeredAt"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("uclistlist", service.getUclistListPageable(pageable));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "uclist/list";
        }

    /** 一覧表示（人気順） */
    @GetMapping("/popular")
    public String getListPopular(@AuthenticationPrincipal UserDetail userDetail, Model model, @ModelAttribute Manga manga, @PageableDefault(page=0, size=20, sort= {"description"}, direction=Direction.DESC) Pageable pageable) {
        model.addAttribute("uclistlist", service.getUclistListPageable(pageable));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        return "uclist/list-popular";
        }

    /** 新規登録（画面表示） */
    @GetMapping("/post")
    public String getRegister(@ModelAttribute Uclist uclist, Model model) {
        List<Manga> mangalist =mangaService.getMangaList();
        model.addAttribute("mangalist", mangalist);
        return "uclist/post-uclist";
    }

    /** 登録処理 */
    @PostMapping("/post")
    public String postRegister(@Validated Uclist uclist, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, @RequestParam("mangaId") List<Integer> mangaIds, Model model, RedirectAttributes attrs) {
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

        attrs.addFlashAttribute("success", "リストが登録されました！");
        return "redirect:/";
    }

}
