package com.sakeman.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.sakeman.dto.WebMangaUpdateInfoAdminResponseDTO;
import com.sakeman.entity.WebMangaUpdateInfo;
import com.sakeman.factory.PageRequestFactory;
import com.sakeman.service.WebMangaUpdateInfoService;

import lombok.RequiredArgsConstructor;



@Controller
@RequestMapping("admin/web-manga-update-info")
@RequiredArgsConstructor
public class AdminWebMangaUpdateInfoController {
    private final WebMangaUpdateInfoService service;
    private final PageRequestFactory pageRequestFactory;

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
    @GetMapping("update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("info", service.getWebMangaUpdateInfo(id));
        return "admin/web-manga-update-info/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}")
    public String updateManga(@PathVariable Integer id, @ModelAttribute WebMangaUpdateInfo info, Model model) {
        service.saveInfo(info);
        return "redirect:/admin/web-manga-update-info/list/modify";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}")
    public String deleteWebMangaUpdateInfo(@PathVariable("id") Integer id) {
        service.deleteById(id);
        return "redirect:/admin/web-manga-update-info/list/modify";
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> getWebMangaUpdateInfos(
        @RequestParam("start") int start,
        @RequestParam("length") int length,
        @RequestParam("search[value]") String searchValue,
        @RequestParam("order[0][column]") int orderColumn,
        @RequestParam("order[0][dir]") String orderDir) {

        String[] columnNames = { "id", "mediaName", "webMangaMedia", "titleString", "manga", "subTitle", "authorString" };
        Pageable pageable = pageRequestFactory.createPageRequest(start, length, orderColumn, orderDir, columnNames);

        Page<WebMangaUpdateInfo> pageData = service.searchWebMangaUpdateInfos(searchValue, pageable);

        List<WebMangaUpdateInfoAdminResponseDTO> responseData = service.getResponseData(pageData);

        Map<String, Object> response = new HashMap<>();
        response.put("data", responseData); // 現在のページデータ
        response.put("recordsTotal", service.countAll()); // 全データ数
        response.put("recordsFiltered", pageData.getTotalElements()); // 検索後のデータ数

        return response;
    }

}