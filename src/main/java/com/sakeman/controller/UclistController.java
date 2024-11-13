package com.sakeman.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.Manga;
import com.sakeman.entity.Review;
import com.sakeman.entity.Uclist;
import com.sakeman.entity.UclistManga;
import com.sakeman.service.MangaService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.UclistMangaService;
import com.sakeman.service.UclistService;
import com.sakeman.service.UserDetail;

import lombok.RequiredArgsConstructor;


@Controller
@RequestMapping("uclist")
@RequiredArgsConstructor
public class UclistController {
    private final UclistService service;
    private final MangaService mangaService;
    private final UclistMangaService umService;
    private final ReadStatusService rsService;

    /** 一覧表示 (全件) */
    @GetMapping({"/list", "/list/{tab}"})
    public String getList(@AuthenticationPrincipal UserDetail userDetail,
                          @ModelAttribute Manga manga,
                          @PathVariable(name="tab", required=false) String tab,
                          @RequestParam(name="page", required=false, defaultValue = "1") int page,
                          Model model) {

        if (tab==null) tab = "recent";
        if (page < 1) {
            return String.format("redirect:/uclist/list/%s", tab);
        }

        Manga mangaobj = null;

        Pageable pageable = getPageable(tab, page);
        Page<Uclist> uclListPages = service.getUclistListPageable(pageable);

        if (page > Math.max(uclListPages.getTotalPages(), 1)) {
            return String.format("redirect:/uclist/list/%s", tab);
        }

        model.addAttribute("uclistlistPages", uclListPages);
        model.addAttribute("uclistlist", uclListPages.getContent());

        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("mangaobj", mangaobj);

        return "uclist/list";
        }

    /** 一覧表示（人気順） */
    @GetMapping({"/list-manga/{manga-id}", "/list-manga/{manga-id}/{tab}"})
    public String getListManga(@AuthenticationPrincipal UserDetail userDetail,
                               @ModelAttribute Manga manga,
                               @PathVariable(name="manga-id", required=true) Integer mangaId,
                               @PathVariable(name="tab", required=false) String tab,
                               @RequestParam(name="page", required=false, defaultValue = "1") int page,
                               Model model) {

        if (tab==null) tab = "recent";
        if (page < 1) {
            return String.format("redirect:/uclist/list-manga/%s/%s", mangaId, tab);
        }

        Manga mangaobj = mangaService.getManga(mangaId);

        Pageable pageable = getPageable(tab, page);
        Page<Uclist> uclistlistPages = service.getByMangaIdPageable(mangaId, pageable);

        if (page > Math.max(uclistlistPages.getTotalPages(), 1)) {
            return String.format("redirect:/uclist/list/%s", tab);
        }

        model.addAttribute("uclistlistPages", uclistlistPages);
        model.addAttribute("uclistlist", uclistlistPages.getContent());

        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));
        model.addAttribute("tab", tab);
        model.addAttribute("mangaobj", mangaobj);

        return "uclist/list";
        }

    /** 新規登録（画面表示） */
    @GetMapping("/post")
//    @PreAuthorize("isAuthenticated")
    public String getRegister(@ModelAttribute Uclist uclist, Model model, RedirectAttributes attrs) {
//        List<Manga> mangalist =mangaService.getMangaList();
//        model.addAttribute("mangalist", mangalist);
        return "uclist/post-uclist";
    }

    /** 登録処理 */
    @PostMapping("/post")
    @PreAuthorize("isAuthenticated")
    public String postRegister(@Validated Uclist uclist, BindingResult res, @AuthenticationPrincipal UserDetail userDetail, @RequestParam("mangaId") List<Integer> mangaIds, Model model, RedirectAttributes attrs) {
        if(res.hasErrors()) {
            return getRegister(uclist, model, attrs);
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

    /** Pageableオブジェクトの取得 */
    public Pageable getPageable(String tab, int page) {
        Pageable pageable;
        switch (tab) {
            case "recent":
                pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "registeredAt"));
                break;
//            case "popular":
//                pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "likes"));
//                break;
            default:
                pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "registeredAt"));
                break;
        }
        return pageable;
    }

}
