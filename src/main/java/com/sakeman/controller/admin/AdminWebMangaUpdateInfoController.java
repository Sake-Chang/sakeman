package com.sakeman.controller.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
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
import com.sakeman.entity.WebMangaMedia;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.MangaService;
import com.sakeman.service.WebMangaMediaService;
import com.sakeman.service.WebMangaUpdateInfoService;



@Controller
@RequestMapping("admin/web-manga-update-info")
public class AdminWebMangaUpdateInfoController {
    private final WebMangaUpdateInfoService service;
    private final WebMangaMediaService mediaService;
    private final MangaService mangaService;


    public AdminWebMangaUpdateInfoController(WebMangaUpdateInfoService service, WebMangaMediaService mediaService, MangaService mangaService) {
        this.service = service;
        this.mediaService = mediaService;
        this.mangaService = mangaService;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        model.addAttribute("pages", service.getInfoListPageable(pageable, false));
        model.addAttribute("infolist", service.getInfoListPageable(pageable, false).getContent());
        return "admin/web-manga-update-info/list";
        }

    /** 一覧表示（mangaIdが1のものだけ） */
    @GetMapping("list/modify")
    public String getListModify(Model model, @PageableDefault(page = 0, size = 50) @SortDefault.SortDefaults({@SortDefault(sort="updateAt", direction=Direction.DESC), @SortDefault(sort="webMangaMedia", direction=Direction.ASC), @SortDefault(sort="id", direction=Direction.DESC)}) Pageable pageable) {
        model.addAttribute("pages", service.findByMangaIdPageable(111111, pageable, false));
        model.addAttribute("infolist", service.findByMangaIdPageable(111111, pageable, false).getContent());

        return "admin/web-manga-update-info/list";
        }

    /** 詳細表示 */
//    @GetMapping("detail/{id}")
//    public String getDetail(@PathVariable("id") Integer id, Model model) {
//        model.addAttribute("info", service.getWebMangaUpdateInfo(id));
//        return "admin/webMangaUpdateInfo/detail";
//        }

    /** 新規登録（画面表示） */
//    @GetMapping("register")
//    public String getRegister(@ModelAttribute WebMangaUpdateInfo info, Model model) {
//        List<WebMangaMedia> medialist = mediaService.getWebMangaMediaList();
//        model.addAttribute("medialist", medialist);
//        return "admin/webMangaUpdateInfo/register";
//    }

    /** 登録処理 */
//    @PostMapping("/register")
//    public String postRegister(@Validated WebMangaUpdateInfo info, @RequestParam("mediaId") List<Integer> mediaIds, BindingResult res, Model model) {
//        if(res.hasErrors()) {
//            return getRegister(info, model);
//        }
//        /** 作品を保存 */
//        service.saveInfo(info);
//
//        return "redirect:/admin/webMangaUpdateInfo/list";
//    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("info", service.getWebMangaUpdateInfo(id));
        return "admin/web-manga-update-info/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateManga(@PathVariable Integer id, @ModelAttribute WebMangaUpdateInfo info, Model model) {
        service.saveInfo(info);
        return "redirect:/admin/web-manga-update-info/list/modify";
    }

    /** 削除処理 */
//    @GetMapping("delete/{id}/")
//    public String deleteManga(@PathVariable("id") Integer id, @ModelAttribute Manga manga, Model model) {
//        Manga mng = service.getManga(id);
//        mng.setDeleteFlag(1);
//        service.saveManga(mng);
//        return "redirect:/admin/manga/list";
//    }
}