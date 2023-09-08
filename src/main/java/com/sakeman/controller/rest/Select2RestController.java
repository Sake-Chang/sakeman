package com.sakeman.controller.rest;

import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaService;


@RestController
@RequestMapping
public class Select2RestController {

    private MangaService maService;
    private AuthorService authService;

    public Select2RestController(MangaService maService, AuthorService authService) {
        this.maService = maService;
        this.authService = authService;
    }

    @GetMapping("/getsearch")
    @ResponseBody
    public List<Manga> select2Search(@RequestParam String q, Model model){
        Manga manga = new Manga();
        manga.setTitle(q);
        List<Manga> searchResult = maService.getSearchResult(manga);
//        model.addAttribute("searchResult", maService.getSearchResult(manga));

        return searchResult;
    }

    @GetMapping("/getsearchauthor")
    @ResponseBody
    public List<Author> select2SearchAuthor(@RequestParam String q, Model model){
        Author author = new Author();
        author.setName(q);
        List<Author> searchResult = authService.getSearchResult(author);
//        model.addAttribute("searchResult", maService.getSearchResult(manga));

        return searchResult;
    }

}
