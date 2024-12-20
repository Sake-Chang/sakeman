package com.sakeman.repository;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import com.sakeman.entity.Manga;

public class MangaSpecifications {
//
//    public static Specification<Manga> searchManga(String[] keywords) {
//        return (root, query, builder) -> {
//            Predicate[] predicates = new Predicate[keywords.length];
//            for (int i = 0; i < keywords.length; i++) {
//                String keyword = "%" + keywords[i].toLowerCase() + "%";
//                predicates[i] = builder.or(
//                        builder.like(builder.lower(root.get("title")), keyword),
//                        builder.like(builder.lower(root.get("synopsis")), keyword),
//                        builder.in(root.join("mangaAuthors").join("author").get("name")).value(keyword)
//                );
//            }
//            return builder.and(predicates);
//        };
//    }

    public static Specification<Manga> searchManga(String[] keywords) {
        return (root, query, builder) -> {
            query.distinct(true);
            var authorJoin = root.join("mangaAuthors").join("author");

            Predicate[] predicates = new Predicate[keywords.length];
            for (int i = 0; i < keywords.length; i++) {
                String keyword = "%" + keywords[i].trim().toLowerCase() + "%";
                predicates[i] = builder.or(
                        builder.like(builder.lower(root.get("title")), keyword),
                        builder.like(builder.lower(root.get("synopsis")), keyword),
                        builder.like(builder.lower(authorJoin.get("name")), keyword)
                );
            }
            return builder.and(predicates);
        };
    }


}
