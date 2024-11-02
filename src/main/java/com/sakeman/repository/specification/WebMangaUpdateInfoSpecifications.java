package com.sakeman.repository.specification;

import java.util.List;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

import org.springframework.data.jpa.domain.Specification;

import com.sakeman.entity.WebMangaUpdateInfo;

public class WebMangaUpdateInfoSpecifications {

    public static Specification<WebMangaUpdateInfo> hasGenres(List<Integer> genreSetting) {
        return (root, query, criteriaBuilder) -> {
            if (genreSetting == null || genreSetting.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            query.distinct(true);
            return root.join("manga").join("mangaTags").join("tag").join("genreTags").join("genre").get("id").in(genreSetting);
        };
    }

    public static Specification<WebMangaUpdateInfo> hasFreeFlags(Integer freeflag) {
        return (root, query, criteriaBuilder) -> (freeflag == null || freeflag == 0)
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get("freeFlag"), 1);
    }

    public static Specification<WebMangaUpdateInfo> followedByUser(Integer followflag, Integer userId) {
        return (root, query, criteriaBuilder) -> (userId == null || followflag == 0)
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.join("manga").join("webMangaFollows").join("user").get("id"), userId);
    }

    public static Specification<WebMangaUpdateInfo> isOneShot(Integer oneshotflag) {
        return (root, query, criteriaBuilder) -> (oneshotflag != null && oneshotflag == 1)
            ? criteriaBuilder.isTrue(criteriaBuilder.and(
                  criteriaBuilder.isNotNull(root.join("manga").get("isOneShot")),
                  root.join("manga").get("isOneShot")
              ))
            : criteriaBuilder.conjunction();
    }
}
