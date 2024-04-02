//package com.sakeman.controller;
//
//import java.util.Date;
//import java.util.Optional;
//import java.util.UUID;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.security.authentication.AnonymousAuthenticationToken;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.sakeman.entity.BadgeUser;
//import com.sakeman.entity.User;
//import com.sakeman.form.ResetPasswordForm;
//import com.sakeman.form.SendEmailForm;
//import com.sakeman.service.BadgeService;
//import com.sakeman.service.BadgeUserService;
//import com.sakeman.service.EmailService;
//import com.sakeman.service.UserDetail;
//import com.sakeman.service.UserService;
//
//import lombok.RequiredArgsConstructor;
//
//@Controller
//@RequiredArgsConstructor
//public class VerificationController {
//    private final UserService userService;
//    private final BadgeService badgeService;
//    private final BadgeUserService buService;
//    private final EmailService emailService;
//    private final PasswordEncoder passwordEncoder;
//
//    @GetMapping("/verify")
//    @Transactional
//    public String verifyUser(@RequestParam("token") String token, Model model, HttpServletRequest request, RedirectAttributes attrs) {
//        // tokenでユーザーを検索
//        User registeredUser = userService.getByVerificationToken(token);
//
//        if (!userService.isValidUserToken(registeredUser)) {
//            model.addAttribute("verificationSuccess", false);
//        } else {
//            // トークンの無効化
//            registeredUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
//            registeredUser.setEnabled(true);
//            userService.saveUser(registeredUser);
//
//            // ユーザーを認証
//            UserDetail userDetail = new UserDetail(registeredUser);
//            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
//            SecurityContext context = SecurityContextHolder.getContext();
//            context.setAuthentication(authentication);
//
//            // バッジの取得と保存
//            Integer uId = registeredUser.getId();
//            if (uId <= 1000 && buService.getByUserIdAndBadgeId(uId, 2).isEmpty()) {
//                BadgeUser badgeUser = new BadgeUser();
//                badgeUser.setBadge(badgeService.getById(2).orElse(null)); // バッジが存在しない場合の考慮
//                badgeUser.setUser(registeredUser);
//                buService.saveBadgeUser(badgeUser);
//                model.addAttribute("success", "称号【アーリーアダプター】を獲得しました！");
//            }
//            model.addAttribute("user", registeredUser);
//            model.addAttribute("verificationSuccess", true);
//        }
//
//        return "verification-result";
//    }
//
//    @PostMapping("/reissue")
//    @Transactional
//    public String reissueToken(@ModelAttribute User user, Model model, HttpServletRequest request, RedirectAttributes attrs) {
//        User regUser = userService.getByEmail(user.getEmail());
//
//        if (regUser == null || !passwordEncoder.matches(user.getPassword(), regUser.getPassword())) {
//            model.addAttribute("errormessage", "メールアドレスかパスワードが違います。");
//            return "verification-result";
//        } else {
//            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
//            regUser.setVerificationToken(UUID.randomUUID().toString());
//            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
//            regUser.setVerificationTokenExpiration(expirationDate);
//            userService.saveUser(regUser);
//
//            String verificationLink = userService.getVerificationLink(request, regUser.getVerificationToken());
//            emailService.sendVerificationEmail(user.getEmail(), verificationLink);
//
//            return "redirect:/confirm";
//        }
//    }
//
//    @GetMapping("/password-reset")
//    public String showPasswordReset(@ModelAttribute SendEmailForm sendEmailForm, Model model) {
////        User user = new User();
////        model.addAttribute("user", user);
//        return "password-reset";
//    }
//
//    @PostMapping("/password-reset")
//    public String sendPasswordReset(@ModelAttribute @Validated SendEmailForm sendEmailForm, BindingResult res, Model model, HttpServletRequest request) {
//        String email = sendEmailForm.getEmail();
//        boolean isEmailExist = isEmailExist(email, res);
//
//        if (res.hasErrors() || !isEmailExist) {
//            return showPasswordReset(sendEmailForm, model);
//        } else {
//            User regUser = userService.getByEmail(email);
//            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
//            regUser.setVerificationToken(UUID.randomUUID().toString());
//            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
//            regUser.setVerificationTokenExpiration(expirationDate);
//            userService.saveUser(regUser);
//
//            String passwordResetLink = userService.getPasswordResetLink(request, regUser.getVerificationToken());
//            emailService.sendPasswordResetEmail(email, passwordResetLink);
//
//            return "redirect:/password-reset/mail-sent";
//        }
//    }
//
//    // パスワードアップデートの場合を記述、共通処理はサービスクラスへ
////    @PostMapping("/password-reset")
////    public String sendPasswordReset(@AuthenticationPrincipal Optional<UserDetail> userDetail, @ModelAttribute @Validated SendEmailForm sendEmailForm, BindingResult res, Model model, HttpServletRequest request) {
////        String email = sendEmailForm.getEmail();
////        boolean isValid;
////        if (userDetail == null) {
////            isValid = true;
////        } else {
////            String checkEmail = userDetail.get().getUser().getEmail();
////            isValid = isValid(email, checkEmail, res);
////        }
////
////        if (res.hasErrors() || !isValid) {
////            return showPasswordReset(sendEmailForm, model);
////        } else {
////            User regUser = userDetail.get().getUser();
////            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
////            regUser.setVerificationToken(UUID.randomUUID().toString());
////            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
////            regUser.setVerificationTokenExpiration(expirationDate);
////            userService.saveUser(regUser);
////
////            String passwordResetLink = userService.getPasswordResetLink(request, regUser.getVerificationToken());
////            emailService.sendPasswordResetEmail(email, passwordResetLink);
////
////            return "redirect:/password-reset/mail-sent";
////        }
////    }
//
//    @GetMapping("/password-reset/mail-sent")
//    public String sendPasswordReset() {
//        return "password-reset-mail-sent";
//    }
//
//    @GetMapping("/password-reset/reset-form")
//    public String showResetForm(@RequestParam(name="token") String token, ResetPasswordForm resetPasswordForm, Model model) {
//        User regUser = userService.getByVerificationToken(token);
//        if (!userService.isValidUserToken(regUser)) {
//            model.addAttribute("verificationSuccess", false);
//        } else {
//           model.addAttribute("verificationSuccess", true);
//        }
//
////        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
//        resetPasswordForm = new ResetPasswordForm();
//        resetPasswordForm.setToken(token);
//        model.addAttribute("resetPasswordForm", resetPasswordForm);
//
//        return "password-reset-form";
//    }
//
//    @PostMapping("/password-reset/reset-form")
//    public String resetPassword(@ModelAttribute @Validated ResetPasswordForm resetPasswordForm, BindingResult res, Model model, RedirectAttributes attrs) {
//        boolean isValid = isValid(resetPasswordForm, res);
//        if (res.hasErrors() || !isValid) {
//            model.addAttribute("resetPasswordForm", resetPasswordForm);
////            return showResetForm(resetPasswordForm.getToken(),resetPasswordForm, model);
//            model.addAttribute("verificationSuccess", true);
//
//            return "password-reset-form";
//        } else {
//            User regUser = userService.getByVerificationToken(resetPasswordForm.getToken());
//            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
//            regUser.setPassword(passwordEncoder.encode(resetPasswordForm.getPassword1()));
//            userService.saveUser(regUser);
//            attrs.addFlashAttribute("success", "パスワードを変更しました！");
//
//            return "redirect:/";
//        }
//    }
//
//    public boolean isValid(ResetPasswordForm resetPasswordForm, BindingResult res) {
//        boolean ans = true;
//
//        String pass1 = resetPasswordForm.getPassword1();
//        String pass2 = resetPasswordForm.getPassword2();
//        String token = resetPasswordForm.getToken();
//
//        if (!pass1.equals(pass2)) {
//            FieldError fieldError = new FieldError(res.getObjectName(), "password2", "確認用パスワードが一致しません");
//            res.addError(fieldError);
//            ans = false;
//        }
//
//        return ans;
//    }
//
//    public boolean isValid(String email, String checkEmail, BindingResult res) {
//        boolean ans = true;
//        if (!email.equals(checkEmail)) {
//            FieldError fieldError = new FieldError(res.getObjectName(), "email", "登録されたメールアドレスと違います");
//            res.addError(fieldError);
//            ans = false;
//        }
//
//        return ans;
//    }
//
//    public boolean isEmailExist(String email, BindingResult res) {
//        boolean ans = true;
//        if (userService.getByEmail(email) == null) {
//            FieldError fieldError = new FieldError(res.getObjectName(), "email", "登録されたメールアドレスではありません");
//            res.addError(fieldError);
//            ans = false;
//        }
//
//        return ans;
//    }
//
//}