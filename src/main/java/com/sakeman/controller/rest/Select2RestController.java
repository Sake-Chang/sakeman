package com.sakeman.controller.rest;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sakeman.entity.Author;
import com.sakeman.entity.Badge;
import com.sakeman.entity.Genre;
import com.sakeman.entity.Manga;
import com.sakeman.entity.Tag;
import com.sakeman.entity.User;
import com.sakeman.service.AuthorService;
import com.sakeman.service.BadgeService;
import com.sakeman.service.GenreService;
import com.sakeman.service.MangaService;
import com.sakeman.service.TagService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping
@RequiredArgsConstructor
public class Select2RestController {

    private final MangaService maService;
    private final AuthorService authService;
    private final TagService tagService;
    private final GenreService genreService;
    private final UserService userService;
    private final BadgeService badgeService;


//    @GetMapping("/getsearch")
//    @ResponseBody
//    public List<Manga> select2Search(@RequestParam(value = "q", defaultValue = "") String q, Model model){
//        Manga manga = new Manga();
//        manga.setTitle(q);
//        List<Manga> searchResult = maService.getSearchResult(manga);
//
////        List<Manga> searchResult = maService.getLikeSearch(q);
//
//        return searchResult;
//    }

    @GetMapping("/select2manga")
    @ResponseBody
    public Page<Manga> select2Search(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Manga manga = new Manga();
        manga.setTitle(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return maService.getSearchResultWithPaging(manga, pageable);
    }

    @GetMapping("/select2author")
    @ResponseBody
    public Page<Author> select2SearchAuthor(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Author author = new Author();
        author.setName(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return authService.getSearchResultWithPaging(author, pageable);
    }

    @GetMapping("/select2tag")
    @ResponseBody
    public Page<Tag> select2SearchTag(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Tag tag = new Tag();
        tag.setTagname(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return tagService.getSearchResultWithPaging(tag, pageable);
    }

    @GetMapping("/select2genre")
    @ResponseBody
    public Page<Genre> select2SearchGenre(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Genre genre = new Genre();
        genre.setName(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return genreService.getSearchResultWithPaging(genre, pageable);
    }

    @GetMapping("/select2user")
    @ResponseBody
    public Page<User> select2SearchUser(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        User user = new User();
        user.setUsername(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return userService.getSearchResultWithPaging(user, pageable);
    }

    @GetMapping("/select2badge")
    @ResponseBody
    public Page<Badge> select2SearchBadge(
            @RequestParam(value = "q", defaultValue = "") String q,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Badge badge = new Badge();
        badge.setName(q);
        Pageable pageable = PageRequest.of(page - 1, size);
        return badgeService.getSearchResultWithPaging(badge, pageable);
    }

}
