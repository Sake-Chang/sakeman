package com.sakeman.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UpdatePasswordForm {
    @NotBlank(message = "空白はNGです！")
    String currentPassword;
    @NotBlank(message = "空白はNGです！")
    String newPassword1;
    @NotBlank(message = "空白はNGです！")
    String newPassword2;
}
