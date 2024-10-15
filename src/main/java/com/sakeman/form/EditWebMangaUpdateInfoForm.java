package com.sakeman.form;

import lombok.Data;

@Data
public class EditWebMangaUpdateInfoForm {

    private Integer id;
    private String mediaName;
    private String titleString;
    private String subTitle;
    private String authorString;

    private Integer mangaId;

}
