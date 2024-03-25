package com.sakeman.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.sakeman.entity.Author;
import com.sakeman.entity.Manga;
import com.sakeman.entity.MangaAuthor;
import com.sakeman.entity.MangaTag;
import com.sakeman.entity.Tag;
import com.sakeman.entity.User;
import com.sakeman.service.AuthorService;
import com.sakeman.service.MangaService;
import com.sakeman.service.MangaTagService;
import com.sakeman.service.TagService;
import com.sakeman.service.UserDetail;

import lombok.RequiredArgsConstructor;



@Controller
@RequestMapping("tag")
@RequiredArgsConstructor
public class TagController {
    private final TagService service;
    private final MangaService mangaService;
    private final MangaTagService mtService;

    /** 登録処理 */
    @PostMapping("/register/{id}")
    @Transactional
    public String postRegister(@PathVariable Integer id, @RequestParam("tagName") List<String> tagNames, Model model, @AuthenticationPrincipal UserDetail userDetail ,final HttpServletRequest request) {

        for(Integer i=0; i < tagNames.size(); i++) {
            User user = userDetail.getUser();
            String tagName = tagNames.get(i);
            List<Tag> thisTags = service.findByTagname(tagName);

            Optional<MangaTag> mangaTag = mtService.findByTagTagnameAndUserIdAndMangaId(tagName, user.getId(), id);
            if (mangaTag.isPresent()) continue;

            MangaTag mt = new MangaTag();
            mt.setManga(mangaService.getManga(id));

            /** タグがあった場合　->　そのタグで登録 */
            if (thisTags.size() != 0) {
                mt.setTag(thisTags.get(0));
                mt.setUser(user);
                mtService.saveMangaTag(mt);
            /** タグがなかった場合 -> 新規タグ登録して登録 */
            } else {
                Tag tag = new Tag();
                tag.setTagname(tagName);
                service.saveTag(tag);

                mt.setTag(tag);
                mt.setUser(user);
                mtService.saveMangaTag(mt);
            }
        }

        String urlraw = request.getHeader("REFERER");
        String url = urlraw.replace("http://localhost:8080", "")
                            .replace("http://sake-man.com", "")
                            .replace("https://sake-man.com", "");

        return "redirect:" + url;
    }
}