package com.sakeman.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("サケマン運営 <info@sake-man.com>");
        message.setSubject("【サケマン】仮登録完了のお知らせ");
        message.setText(
                "この度はサケマンにご登録いただき、ありがとうございます！\n"
                + "現在はまだ仮登録の状態です。\n"
                + "以下のリンクをクリックして本登録をお願いします。\n"
                + verificationLink
                + "\n\n※このメールは、サケマンへ登録された方に自動送信しています。本メールにお心当りがない場合は、誠に恐れ入りますが弊社サポートまでお問い合わせくださいますようお願いいたします。\n"
                + "※このメールに記載されたURLの有効期限は1時間です。有効期限切れの場合は、お手数ですが上記リンクから改めて手続を行ってください。");
        javaMailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String to, String passwordResetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom("サケマン運営 <info@sake-man.com>");
        message.setSubject("【サケマン】パスワード再設定のご案内");
        message.setText(
                "いつもサケマンをご利用いただき、ありがとうございます！\n"
                + "以下のリンクをクリックすると、現在のパスワードがリセットされ、\n"
                + "新しいパスワードを再設定できます。\n"
                + passwordResetLink
                + "\n\n※このメールにお心当りがない場合は、URLにアクセスせず、本メールを破棄していただきますようお願いいたします。\n"
                + "※このメールに記載されたURLの有効期限は1時間です。有効期限切れの場合は、お手数ですが上記リンクから改めて手続を行ってください。");
        javaMailSender.send(message);    }
}
