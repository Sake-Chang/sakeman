package com.sakeman.dto;

import lombok.Data;

@Data
public class WebMangaUpdateInfoAdminResponseDTO {
    String[] columnNames = { "id", "mediaName", "webMangaMedia", "titleString", "manga", "subTitle", "authorString" };

    private final Integer id;
    private final String mediaName;
    private final Integer webMangaMedia;
    private final String titleString;
    private final Integer manga;
    private final String subTitle;
    private final String authorString;

}
