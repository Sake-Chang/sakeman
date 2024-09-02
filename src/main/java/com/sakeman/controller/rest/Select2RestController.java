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

    @GetMapping("/getsearch")
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

    @GetMapping("/getsearchauthor")
    @ResponseBody
    public List<Author> select2SearchAuthor(@RequestParam(value = "q", defaultValue = "") String q, Model model){
        Author author = new Author();
        author.setName(q);
        List<Author> searchResult = authService.getSearchResult(author);
//        model.addAttribute("searchResult", maService.getSearchResult(manga));

        return searchResult;
    }

    @GetMapping("/getsearchtag")
    @ResponseBody
    public List<Tag> select2SearchTag(@RequestParam(value = "q", defaultValue = "") String q, Model model){
        Tag tag = new Tag();
        tag.setTagname(q);
        List<Tag> searchResult = tagService.getSearchResult(tag);

        return searchResult;
    }

    @GetMapping("/getsearchgenre")
    @ResponseBody
    public List<Genre> select2SearchGenre(@RequestParam(value = "q", defaultValue = "") String q, Model model){
        Genre genre = new Genre();
        genre.setName(q);
        List<Genre> searchResult = genreService.getSearchResult(genre);

        return searchResult;
    }

    @GetMapping("/getsearchuser")
    @ResponseBody
    public List<User> select2SearchUser(@RequestParam(value = "q", defaultValue = "") String q, Model model){
        User user = new User();
        user.setUsername(q);
        List<User> searchResult = userService.getSearchResult(user);

        return searchResult;
    }

    @GetMapping("/getsearchbadge")
    @ResponseBody
    public List<Badge> select2SearchBadge(@RequestParam(value = "q", defaultValue = "") String q, Model model){
        Badge badge = new Badge();
        badge.setName(q);
        List<Badge> searchResult = badgeService.getSearchResult(badge);

        return searchResult;
    }


}
