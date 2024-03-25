package com.sakeman.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ResetPasswordForm {
    @NotBlank(message = "空白はNGです！")
    String password1;
    @NotBlank(message = "空白はNGです！")
    String password2;
    String token;

}
