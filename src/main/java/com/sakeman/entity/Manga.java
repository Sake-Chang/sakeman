package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "manga")
@ToString(exclude = {"reviews", "mangaAuthors", "uclistMangas", "webMangaFollows"})
@JsonIgnoreProperties({"titleKana", "registeredAt", "updatedAt", "displayFlag", "deleteFlag", "completionFlag", "volume", "publisher", "publishedIn", "synopsis", "calligraphy", "csid", "reviews", "mangaAuthors", "mangaTags", "webMangaUpdateInfos", "webMangaFollows", "uclistMangas", "readStatus"})
@Where(clause = "delete_flag=0")
public class Manga implements Serializable {

    /** フィールド */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "title_cleanse", nullable = true)
    private String titleCleanse;

    @Column(name = "title_kana")
    private String titleKana;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;

    @Column(name = "display_flag", nullable = true)
    private Integer displayFlag;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    @Column(name = "completion_flag", nullable = true)
    private Integer completionFlag;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "publisher", nullable = true)
    private String publisher;

    @Column(name = "published_in")
    private String publishedIn;

    @Column(name = "synopsis", nullable = true)
    @Type(type = "text")
    private String synopsis;

    @Column(name = "calligraphy", nullable = true)
    private String calligraphy;

    @Column(name = "csid")
    private Integer csid;

    /** review */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    private List<Review> reviews;

    /** manga_author */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<MangaAuthor> mangaAuthors;

    /** webMangaFollows */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<WebMangaFollow> webMangaFollows;

    /** manga_tag */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<MangaTag> mangaTags;

    /** webMangaUpdateInfo */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    private List<WebMangaUpdateInfo> webMangaUpdateInfos;

    /** uclist_manga */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<UclistManga> uclistMangas;

//    /** user_manga */
//    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
//    private List<UserManga> userMangas;

    /** readStatus */
    @OrderBy(value = "status.読みたい.length desc")
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<ReadStatus> readStatus;


    /** メソッド */

    @PrePersist
    public void onPrePersist() {
        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

//    @JsonValue
//    public Map<String, Object> toJson() {
//        Map<String, Object> map = new LinkedHashMap<>();
//        map.put("id", id);
//        map.put("title", title);
//        return map;
//    }

}
