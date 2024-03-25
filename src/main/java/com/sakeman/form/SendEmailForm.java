package com.sakeman.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class SendEmailForm {
    @NotBlank(message = "必須項目です。")
    @Email(message = "ちゃんとしたメールアドレスをお願いします！")
    String email;

}
