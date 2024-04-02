package com.sakeman.service;

import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.sakeman.entity.User;
import com.sakeman.form.ReissueTokenForm;
import com.sakeman.form.ResetPasswordForm;
import com.sakeman.form.SignupForm;
import com.sakeman.form.UpdateEmailForm;
import com.sakeman.form.UpdatePasswordForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfService {

    public final UserService userService;
    public final PasswordEncoder passwordEncoder;

    public String getVerificationLink(HttpServletRequest request, String token) {
        // ベースURLの取得
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // リンクの構築
        return baseUrl + "/verify?token=" + token;
    }

    public String getPasswordResetLink(HttpServletRequest request, String token) {
        // ベースURLの取得
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // リンクの構築
        return baseUrl + "/reset-password?token=" + token;
    }

    public String getUpdateEmailLink(HttpServletRequest request, String token) {
        // ベースURLの取得
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        // リンクの構築
        return baseUrl + "/verify-email?token=" + token;
    }

    // トークンが有効であるかを確認
    public boolean isValidUserToken(Optional<User> userOpt) {
        return userOpt.map(user ->
                User.VerificationTokenStatus.VALID.equals(user.getVerificationTokenStatus()) &&
                user.getVerificationTokenExpiration() != null &&
                new Date().before(user.getVerificationTokenExpiration())
            ).orElse(false);
    }

    public boolean isValidSignupForm(SignupForm signupForm, BindingResult res) {
        boolean ans = true;

        String email = signupForm.getEmail();
        String username = signupForm.getUsername();
        Optional<User> regUserOpt = userService.getByEmail(email);
        if (regUserOpt.isPresent()) {
            FieldError fieldError = new FieldError(res.getObjectName(), "email", "このメールアドレスは既に登録されています");
            res.addError(fieldError);
            ans = false;
        }
        if (username.matches("[\\s　]*")) {
            FieldError fieldError = new FieldError(res.getObjectName(), "username", "空白はNGです！");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

    public boolean isValidResetPasswordForm(ResetPasswordForm resetPasswordForm, BindingResult res) {
        boolean ans = true;

        String pass1 = resetPasswordForm.getPassword1();
        String pass2 = resetPasswordForm.getPassword2();
        String token = resetPasswordForm.getToken();
        Optional<User> regUserOpt = userService.getByVerificationToken(token);

        if (!pass1.equals(pass2)) {
            FieldError fieldError = new FieldError(res.getObjectName(), "password2", "確認用パスワードが一致しません");
            res.addError(fieldError);
            ans = false;
        }
        if (regUserOpt.isEmpty()) {
            FieldError fieldError = new FieldError(res.getObjectName(), "password2", "確認用パスワードが一致しません");
            res.addError(fieldError);
            ans = false;        }
        return ans;
    }

    public boolean isValidUpdatePasswordForm(UpdatePasswordForm updatePasswordForm, BindingResult res, UserDetail userDetail) {
        boolean ans = true;

        String pass1 = updatePasswordForm.getNewPassword1();
        String pass2 = updatePasswordForm.getNewPassword2();
        String currentPass = updatePasswordForm.getCurrentPassword();

        if (!pass1.equals(pass2)) {
            FieldError fieldError = new FieldError(res.getObjectName(), "newPassword2", "確認用パスワードが一致しません");
            res.addError(fieldError);
            ans = false;
        }
        if (!passwordEncoder.matches(currentPass, userDetail.getPassword())) {
            FieldError fieldError = new FieldError(res.getObjectName(), "currentPassword", "いまのパスワードが違います");
            res.addError(fieldError);
            ans = false;        }
        return ans;
    }

    public boolean isValidUpdateEmailForm(UpdateEmailForm updateEmailForm, BindingResult res, UserDetail userDetail) {
        boolean ans = true;

        String enteredPass = updateEmailForm.getPassword();
        String currentPass = userDetail.getPassword();

        if (!passwordEncoder.matches(enteredPass, currentPass)) {
            FieldError fieldError = new FieldError(res.getObjectName(), "password", "パスワードが違います");
            res.addError(fieldError);
            ans = false;
        }
        if (userService.getByEmail(updateEmailForm.getEmail()).isPresent()) {
            FieldError fieldError = new FieldError(res.getObjectName(), "email", "このメールアドレスは既に登録されています");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

    public boolean isValidReissueTokenForm(ReissueTokenForm reissueTokenForm, BindingResult res) {
        boolean ans = true;
        Optional<User> regUserOpt = userService.getByEmail(reissueTokenForm.getEmail());
        String pass = reissueTokenForm.getPassword();

        if (regUserOpt.isEmpty()) {
            FieldError fieldError = new FieldError(res.getObjectName(), "email", "このメールアドレスは登録されていません");
            res.addError(fieldError);
            ans = false;
        } else if (!passwordEncoder.matches(pass, regUserOpt.get().getPassword())) {
            FieldError fieldError = new FieldError(res.getObjectName(), "password", "パスワードが違います");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

    public boolean isEmailExist(String email, BindingResult res) {
        boolean ans = true;
        Optional<User> user = userService.getByEmail(email);
        if (user.isEmpty()) {
            FieldError fieldError = new FieldError(res.getObjectName(), "email", "登録されたメールアドレスではありません");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }

    public boolean isValidPassword(UpdatePasswordForm updatePasswordForm, BindingResult res, UserDetail userDetail) {
        boolean ans = true;
        String enteredPassword = updatePasswordForm.getCurrentPassword();
        String registeredPassword = userDetail.getPassword();
        if (!enteredPassword.equals(registeredPassword)) {
            FieldError fieldError = new FieldError(res.getObjectName(), "currentPassword", "パスワードが間違っています");
            res.addError(fieldError);
            ans = false;
        }
        return ans;
    }
}
