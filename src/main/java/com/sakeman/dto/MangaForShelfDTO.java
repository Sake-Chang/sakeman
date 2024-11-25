package com.sakeman.dto;

import java.util.List;

import com.sakeman.entity.Author;

import lombok.Data;

@Data
public class MangaForShelfDTO {
    private final Integer id;
    private final String title;
    private final String calligraphy;
    private final Integer completionFlag;
    private final Integer volume;

    private final Integer userRating;
    private final boolean isReviewed;
    private final Integer reviewCount;
    private final double averageRating;

    private final List<Author> authors;


}
