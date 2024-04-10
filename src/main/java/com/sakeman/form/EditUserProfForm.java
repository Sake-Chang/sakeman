package com.sakeman.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class EditUserProfForm {

    private Integer id;

    private String img;

    @NotBlank(message = "必須項目です。")
    @Size(min = 1, max = 50, message = "1~50文字でお願いします！")
    private String username;

    @Size(max = 500, message = "500文字以下でお願いします！")
    private String selfIntro;

    private MultipartFile fileContents;

}
