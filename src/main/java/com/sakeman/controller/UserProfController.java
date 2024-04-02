package com.sakeman.controller;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sakeman.entity.BadgeUser;
import com.sakeman.entity.User;
import com.sakeman.form.ReissueTokenForm;
import com.sakeman.form.ResetPasswordForm;
import com.sakeman.form.SendEmailForm;
import com.sakeman.form.SignupForm;
import com.sakeman.form.UpdateEmailForm;
import com.sakeman.form.UpdatePasswordForm;
import com.sakeman.service.BadgeService;
import com.sakeman.service.BadgeUserService;
import com.sakeman.service.EmailService;
import com.sakeman.service.UserDetail;
import com.sakeman.service.UserProfService;
import com.sakeman.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class UserProfController {
    private final UserService userService;
    private final UserProfService userProfService;
    private final BadgeService badgeService;
    private final BadgeUserService buService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    /** サインアップ (画面表示) */
    @GetMapping("/signup")
    public String getSignup(@ModelAttribute SignupForm signupForm, Model model) {
        return "crud-userprof/signup";
    }

    /** サインアップ (登録処理) */
    @PostMapping("/signup")
    public String postSignup(@Validated SignupForm signupForm, BindingResult res, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        boolean isValid = userProfService.isValidSignupForm(signupForm, res);
        if(res.hasErrors() | !isValid) {
            return getSignup(signupForm, model);
        }
        try {
            /** ユーザーを保存 */
            User user = signupForm.toUser();
            user.setDeleteFlag(0);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(User.Role.一般);
            user.setEnabled(false);
            user.setVerificationToken(UUID.randomUUID().toString());
            user.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
            user.setVerificationTokenExpiration(expirationDate);

            userService.saveUser(user);

            /** 認証メールを送信 */
            User regUser = userService.getByEmail(user.getEmail()).get();
            String verificationLink = userProfService.getVerificationLink(request, regUser.getVerificationToken());
            emailService.sendUserVerificationEmail(regUser.getEmail(), verificationLink);

        } catch (DataAccessException e) {
            model.addAttribute("signupError", "このメールアドレスは既に登録されています。");
            return "crud-userprof/signup";
        }
        attrs.addFlashAttribute("title", "仮登録完了");
        attrs.addFlashAttribute("h2", "仮登録が完了しました");
        attrs.addFlashAttribute("text", "メールアドレス認証用のメールを送信しました。<br>メールの内容に沿って、登録を完了してください。");
        return "redirect:/sent-email";
    }

    /** メール送信完了画面の表示 */
    @GetMapping("/sent-email")
    public String cofirm() {
        return "crud-userprof/sent-email";
    }

    /** メールリンク クリックした画面 */
    @GetMapping("/verify")
    @Transactional
    public String verifyUser(@RequestParam("token") String token, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        Optional<User> regUserOpt = userService.getByVerificationToken(token);

        if (!userProfService.isValidUserToken(regUserOpt)) {
            model.addAttribute("verificationSuccess", false);
            model.addAttribute("reissueTokenForm", new ReissueTokenForm());
            model.addAttribute("h2", "認証エラー");
            model.addAttribute("text", "リンクが無効または期限切れです。<br>以下のフォームから再手続をしてください。");
        } else {
            User regUser = regUserOpt.get();
            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
            regUser.setEnabled(true);
            userService.saveUser(regUser);

            // ユーザーをログイン状態に
            UserDetail userDetail = new UserDetail(regUser);
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);

            // バッジの取得と保存
            Integer uId = regUser.getId();
            if (uId <= 10000 && buService.getByUserIdAndBadgeId(uId, 2).isEmpty()) {
                BadgeUser badgeUser = new BadgeUser();
                badgeUser.setBadge(badgeService.getById(2).orElse(null)); // バッジが存在しない場合の考慮
                badgeUser.setUser(regUser);
                buService.saveBadgeUser(badgeUser);
                model.addAttribute("success", "称号【アーリーアダプター】を獲得しました！");
            }
            model.addAttribute("user", regUser);
            model.addAttribute("verificationSuccess", true);
            model.addAttribute("h2", "ユーザー登録が完了しました！");
            model.addAttribute("text", "アカウントが有効になりました。<br>サケマンをお楽しみください！");
        }

        return "crud-userprof/verify-token";
    }

    /** サインアップ時のトークンエラーの際のトークン再発行 */
    @PostMapping("/reissue")
    @Transactional
    public String reissueToken(@Validated ReissueTokenForm reissueTokenForm, BindingResult res, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        boolean isValid = userProfService.isValidReissueTokenForm(reissueTokenForm, res);
        String email = reissueTokenForm.getEmail();

        if (res.hasErrors()|| !isValid) {
            model.addAttribute("h2", "認証メール再発行");
            model.addAttribute("text", "エラーが発生しました。<br>内容を確認してください。");
            return "crud-userprof/verify-token";
        } else {
            userService.getByEmail(email).ifPresent(regUser -> {
                Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
                regUser.setVerificationToken(UUID.randomUUID().toString());
                regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
                regUser.setVerificationTokenExpiration(expirationDate);
                userService.saveUser(regUser);

                String verificationLink = userProfService.getVerificationLink(request, regUser.getVerificationToken());
                emailService.sendUserVerificationEmail(email, verificationLink);

            });
            attrs.addFlashAttribute("title", "メール送信完了");
            attrs.addFlashAttribute("h2", "メールを送信しました");
            attrs.addFlashAttribute("text", "メールアドレス認証用のメールを送信しました。<br>メールの内容に沿って、登録を完了してください。");
            return "redirect:/sent-email";
        }
    }

    /** パスワードリセット (画面表示) */
    @GetMapping("/reset-password-request")
    public String showPasswordReset(@ModelAttribute SendEmailForm sendEmailForm, Model model) {
        return "crud-userprof/reset-password-request";
    }

    /** パスワードリセット (メールリンク送信) */
    @PostMapping("/reset-password-request")
    public String sendPasswordReset(@Validated SendEmailForm sendEmailForm, BindingResult res, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        String email = sendEmailForm.getEmail();
        boolean isEmailExist = userProfService.isEmailExist(email, res);

        if (res.hasErrors() || !isEmailExist) {
            return showPasswordReset(sendEmailForm, model);
        } else {
            userService.getByEmail(email).ifPresent(regUser -> {
                Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
                regUser.setVerificationToken(UUID.randomUUID().toString());
                regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
                regUser.setVerificationTokenExpiration(expirationDate);
                userService.saveUser(regUser);

                String passwordResetLink = userProfService.getPasswordResetLink(request, regUser.getVerificationToken());
                emailService.sendPasswordResetEmail(email, passwordResetLink);
        });
            attrs.addFlashAttribute("title", "メール送信完了");
            attrs.addFlashAttribute("h2", "メールを送信しました");
            attrs.addFlashAttribute("text", "パスワード再設定用のメールを送信しました。<br>メールの内容に沿って、再設定してください。");
            return "redirect:/sent-email";
        }
    }

    /** パスワードリセット (メールリンククリック) */
    @GetMapping("/reset-password")
    public String showResetForm(@RequestParam(name="token") String token, ResetPasswordForm resetPasswordForm, Model model) {
        Optional<User> regUserOpt = userService.getByVerificationToken(token);
        if (!userProfService.isValidUserToken(regUserOpt)) {
            model.addAttribute("verificationSuccess", false);
        } else {
           model.addAttribute("verificationSuccess", true);
        }

        resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setToken(token);
        model.addAttribute("resetPasswordForm", resetPasswordForm);

        return "crud-userprof/reset-password-form";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@Validated ResetPasswordForm resetPasswordForm, BindingResult res, Model model, RedirectAttributes attrs) {
        Optional<User> regUserOpt = userService.getByVerificationToken(resetPasswordForm.getToken());
        boolean isValid = userProfService.isValidResetPasswordForm(resetPasswordForm, res);
        boolean isValidUserToken = userProfService.isValidUserToken(regUserOpt);
        if (res.hasErrors() || !isValid || !isValidUserToken) {
            model.addAttribute("resetPasswordForm", resetPasswordForm);
            model.addAttribute("verificationSuccess", true);
            return "crud-userprof/reset-password-form";
        } else {
            User regUser = regUserOpt.get();
            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
            regUser.setPassword(passwordEncoder.encode(resetPasswordForm.getPassword1()));
            userService.saveUser(regUser);
            attrs.addFlashAttribute("success", "新しいパスワードを設定しました！<br>ログインしてお楽しみください！");
            return "redirect:/login";
        }
    }

    /** パスワードアップデート */
    @GetMapping("/update-password")
    public String showUpdateForm(@ModelAttribute UpdatePasswordForm updatePasswordForm, Model model) {
        return "crud-userprof/update-password-form";
    }

    @PostMapping("/update-password")
    public String updateEmail(@Validated UpdatePasswordForm updatePasswordForm,
                                 BindingResult res,
                                 @AuthenticationPrincipal UserDetail userDetail,
                                 Model model,
                                 RedirectAttributes attrs,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        boolean isValid = userProfService.isValidUpdatePasswordForm(updatePasswordForm, res, userDetail);
        if (res.hasErrors() || !isValid) {
            model.addAttribute("updatePasswordForm", updatePasswordForm);
            return "crud-userprof/update-password-form";
        } else {
            User regUser = userDetail.getUser();
            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
            regUser.setPassword(passwordEncoder.encode(updatePasswordForm.getNewPassword1()));
            userService.saveUser(regUser);

            new SecurityContextLogoutHandler().logout(request, response, null);
            attrs.addFlashAttribute("success", "パスワードを変更しました！<br>再ログインしてお楽しみください。");
            return "redirect:/login";

        }
    }

    /** メールアドレスの変更 */
    @GetMapping("/update-email")
    public String showUpdateForm(@ModelAttribute UpdateEmailForm updateEmailForm, Model model) {
        return "crud-userprof/update-email-form";
    }

    @PostMapping("/update-email")
    public String updatePassword(@Validated UpdateEmailForm updateEmailForm,
                                 BindingResult res,
                                 @AuthenticationPrincipal UserDetail userDetail,
                                 Model model,
                                 RedirectAttributes attrs,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        boolean isValid = userProfService.isValidUpdateEmailForm(updateEmailForm, res, userDetail);
        if (res.hasErrors() || !isValid) {
            model.addAttribute("updateEmailForm", updateEmailForm);
            return "crud-userprof/update-email-form";
        } else {
            User regUser = userDetail.getUser();
            regUser.setNewEmail(updateEmailForm.getEmail());
            regUser.setVerificationToken(UUID.randomUUID().toString());
            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.VALID);
            Date expirationDate = new Date(System.currentTimeMillis() + 3600000);
            regUser.setVerificationTokenExpiration(expirationDate);

            userService.saveUser(regUser);

            /** 認証メールを送信 */
            String verificationLink = userProfService.getUpdateEmailLink(request, regUser.getVerificationToken());
            emailService.sendNewEmailVerificationEmail(regUser.getNewEmail(), verificationLink);

            attrs.addFlashAttribute("title", "メール送信完了");
            attrs.addFlashAttribute("h2", "メールを送信しました");
            attrs.addFlashAttribute("text", "メールアドレス変更用のメールを送信しました。<br>メールの内容に沿って、手続をしてください。");
            return "redirect:/sent-email";
        }
    }

    @GetMapping("/verify-email")
    @Transactional
    public String verifyEmail(@RequestParam("token") String token, Model model, HttpServletRequest request, RedirectAttributes attrs) {
        Optional<User> regUserOpt = userService.getByVerificationToken(token);

        if (!userProfService.isValidUserToken(regUserOpt)) {
            model.addAttribute("verificationSuccess", false);
            model.addAttribute("mode", "verify-email");
            model.addAttribute("updateEmailForm", new UpdateEmailForm());
            model.addAttribute("h2", "認証エラー");
            model.addAttribute("text", "リンクが無効または期限切れです。<br>以下のフォームから再手続をしてください。");
        } else {
            User regUser = regUserOpt.get();
            regUser.setVerificationTokenStatus(User.VerificationTokenStatus.INVALID);
            regUser.setEmail(regUser.getNewEmail());
            userService.saveUser(regUser);

            model.addAttribute("user", regUser);
            model.addAttribute("verificationSuccess", true);
            model.addAttribute("h2", "変更完了");
            model.addAttribute("text", "メールアドレスが変更されました。<br>引き続きサケマンをお楽しみください！");
        }
        return "crud-userprof/verify-token";
    }

}

