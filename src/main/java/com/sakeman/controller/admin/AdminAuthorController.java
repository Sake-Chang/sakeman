package com.sakeman.controller.admin;

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

import com.sakeman.entity.Author;
import com.sakeman.service.AuthorService;



@Controller
@RequestMapping("admin/author")
public class AdminAuthorController {
    private final AuthorService service;

    public AdminAuthorController(AuthorService service) {
        this.service = service;
    }

//    /** 一覧表示 */
//    @GetMapping("list")
//    public String getList(Model model) {
//        model.addAttribute("authorlist", service.getAuthorList());
//        return "admin/author/list";
//        }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model,  @PageableDefault(page=0, size=100, sort= {"id"}, direction=Direction.ASC) Pageable pageable) {
        model.addAttribute("pages", service.getAuthorListPageable(pageable));
        model.addAttribute("authorlist", service.getAuthorListPageable(pageable).getContent());
        return "admin/author/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("author", service.getAuthor(id));
        return "admin/author/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute Author author, Model model) {
        return "admin/author/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated Author author, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(author, model);
        }
        author.setDeleteFlag(0);
        author.setDisplayFlag(1);
        service.saveAuthor(author);
        return "redirect:/admin/author/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("author", service.getAuthor(id));
        return "admin/author/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateAuthor(@PathVariable Integer id, @ModelAttribute Author author, Model model) {
        service.saveAuthor(author);
        return "redirect:/admin/author/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteAuthor(@PathVariable("id") Integer id, @ModelAttribute Author author, Model model) {
        Author atr = service.getAuthor(id);
        atr.setDeleteFlag(1);
        service.saveAuthor(atr);
        return "redirect:/admin/author/list";
    }
}