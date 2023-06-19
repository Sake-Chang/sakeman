package com.sakeman.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sakeman.entity.User;
import com.sakeman.service.UserService;

@Controller
@RequestMapping("admin/user")
public class AdminUserController {
    private final UserService service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AdminUserController(UserService service) {
        this.service = service;
    }

    /** 一覧表示 */
    @GetMapping("list")
    public String getList(Model model) {
        model.addAttribute("userlist", service.getUserList());
        return "admin/user/list";
        }

    /** 詳細表示 */
    @GetMapping("detail/{id}")
    public String getDetail(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("user", service.getUser(id));
        return "admin/user/detail";
        }

    /** 新規登録（画面表示） */
    @GetMapping("register")
    public String getRegister(@ModelAttribute User user, Model model) {
        return "admin/user/register";
    }

    /** 登録処理 */
    @PostMapping("/register")
    public String postRegister(@Validated User user, BindingResult res, Model model) {
        if(res.hasErrors()) {
            return getRegister(user, model);
        }
        user.setDeleteFlag(0);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        service.saveUser(user);
        return "redirect:/admin/user/list";
    }

    /** 編集画面を表示 */
    @GetMapping("update/{id}/")
    public String getUpdate(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("user", service.getUser(id));
        return "admin/user/update";
    }

    /** 編集処理 */
    @PostMapping("update/{id}/")
    public String updateUser(@PathVariable Integer id, @ModelAttribute User user, Model model) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        service.saveUser(user);
        return "redirect:/admin/user/list";
    }

    /** 削除処理 */
    @GetMapping("delete/{id}/")
    public String deleteUser(@PathVariable("id") Integer id, @ModelAttribute User user, Model model) {
        User usr = service.getUser(id);
        usr.setDeleteFlag(1);
        service.saveUser(usr);
        return "redirect:/admin/user/list";
    }
}