package com.sakeman.controller;

import java.util.Collections;
import java.util.List;
import java.util.Random;

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

import com.sakeman.entity.Author;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.service.AuthorFollowService;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaAuthorService;
import com.sakeman.service.ReadStatusService;
import com.sakeman.service.UserDetail;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("author")
@RequiredArgsConstructor
public class AuthorController {
    private final AuthorService service;
    private final AuthorFollowService afService;
    private final MangaAuthorService maService;
    private final ReadStatusService rsService;

    /** 一覧表示（Pageable） */
    @GetMapping("/list")
    public String getList(Model model,  @PageableDefault(page=0, size=100, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getAuthorListPageable(pageable));
        model.addAttribute("authorlist", service.getAuthorListPageable(pageable).getContent());
        return "author/list";
        }

    /** 詳細表示 */
    @GetMapping("/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model, @AuthenticationPrincipal UserDetail userDetail) {
        List<MangaAuthor> mangalist = maService.findByAuthorId(id);
        Collections.shuffle(mangalist);

        model.addAttribute("author", service.getAuthor(id));
        model.addAttribute("mangalist", mangalist);
        model.addAttribute("authorlist", afService.authorIdListFollowedByUser(userDetail));
        model.addAttribute("wantlist", rsService.getWantMangaIdByUser(userDetail));
        model.addAttribute("readlist", rsService.getReadMangaIdByUser(userDetail));

        return "author/detail";
        }
}