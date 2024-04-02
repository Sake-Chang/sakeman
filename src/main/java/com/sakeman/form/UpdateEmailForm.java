package com.sakeman.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdateEmailForm {
    @NotBlank(message = "空白はNGです！")
    @Email(message = "ちゃんとしたメールアドレスをお願いします！")
    String email;
    @NotBlank(message = "空白はNGです！")
    String password;
    String token;
}
