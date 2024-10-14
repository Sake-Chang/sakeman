package com.sakeman.controller.admin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.sakeman.dto.MangaAdminResponseDTO;
import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.factory.PageRequestFactory;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.MangaService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
@RequestMapping("admin/manga")
public class AdminMangaController {
    private final MangaService service;
    private final AuthorService authService;
    private final MangaAuthorService maService;
    private final PageRequestFactory pageRequestFactory;

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
    public String postRegister(@Validated Manga inputData, @RequestParam("authorName") List<String> authorNames, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(inputData, model);
        }

        Set<String> inputAuthorNames = new HashSet<>(authorNames);
        List<MangaAuthor> malistToAdd = maService.getMalistToAdd(inputAuthorNames, inputData);

        service.setMangaDetailAndSave(inputData, malistToAdd);

        return "redirect:/admin/manga/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("manga", service.getManga(id));
        return "admin/manga/update";
    }

    /** 編集処理 */
    @PostMapping("update/{mangaId}")
    @Transactional
    public String updateManga(@PathVariable Integer mangaId, @ModelAttribute Manga inputData, @RequestParam("authorName") List<String> authorNames, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getUpdate(mangaId, model);
        }

        Manga existingManga = service.getManga(mangaId);
        Set<String> inputAuthorNames = new HashSet<>(authorNames);
        List<MangaAuthor> currentMangaAuthors = existingManga.getMangaAuthors();

        List<MangaAuthor> malistToRemove = maService.getMalistToRemove(currentMangaAuthors, inputAuthorNames);
        maService.deleteAll(malistToRemove);

        List<MangaAuthor> newMangaAuthorList = maService.getMalistToAdd(inputAuthorNames, existingManga);

        currentMangaAuthors.removeAll(malistToRemove);
        currentMangaAuthors.addAll(newMangaAuthorList);

        service.updateMangaDetail(existingManga, inputData, currentMangaAuthors);

        return "redirect:/admin/manga/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}")
    public String deleteManga(@PathVariable("id") Integer id, @ModelAttribute Manga manga, Model model) {
        Manga mng = service.getManga(id);
        mng.setDeleteFlag(1);
        service.saveManga(mng);
        return "redirect:/admin/manga/list";
    }

    @GetMapping("/api")
    @ResponseBody
    public Map<String, Object> getMangas(
        @RequestParam("start") int start,
        @RequestParam("length") int length,
        @RequestParam("search[value]") String searchValue,
        @RequestParam("order[0][column]") int orderColumn,
        @RequestParam("order[0][dir]") String orderDir) {

        String[] columnNames = { "id", "title", "authors", "volume", "publisher", "publishedIn" };
        Pageable pageable = pageRequestFactory.createPageRequest(start, length, orderColumn, orderDir, columnNames);

        Page<Manga> pageData = service.searchMangas(searchValue, pageable);

        List<MangaAdminResponseDTO> responseData = service.getResponseData(pageData);

        Map<String, Object> response = new HashMap<>();
        response.put("data", responseData); // 現在のページデータ
        response.put("recordsTotal", service.countAll()); // 全データ数
        response.put("recordsFiltered", pageData.getTotalElements()); // 検索後のデータ数

        return response;
    }

}