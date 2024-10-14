package com.sakeman.dto;

import lombok.Data;

@Data
public class MangaAdminResponseDTO {
    private final Integer id;
    private final String title;
    private final Integer volume;
    private final String publisher;
    private final String publishedIn;
    private final String authors;

}
