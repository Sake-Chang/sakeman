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
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "manga")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Where(clause = "delete_flag=0")
public class Manga implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "title_cleanse", nullable = true)
    private String titleCleanse;

    @Column(name = "title_kana", nullable = true)
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

    @Column(name = "isOneShot", nullable = true)
    private Boolean isOneShot;

    @Column(name = "volume", nullable = false)
    private Integer volume;

    @Column(name = "publisher", nullable = true)
    private String publisher;

    @Column(name = "published_in", nullable = true)
    private String publishedIn;

    @Column(name = "synopsis", nullable = true)
    @Type(type = "text")
    private String synopsis;

    @Column(name = "calligraphy", nullable = true)
    private String calligraphy;

    @Column(name = "csid", nullable = true)
    private Integer csid;


    /** 関連エンティティ */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("manga-authors")
    private List<MangaAuthor> mangaAuthors;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    @JsonManagedReference("manga-reviews")
    private List<Review> reviews;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    @JsonManagedReference("manga-uclists")
    List<UclistManga> uclistMangas;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    @JsonManagedReference("manga-webMangaFollows")
    private List<WebMangaFollow> webMangaFollows;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    @JsonManagedReference("manga-webMangaUpdateInfos")
    private List<WebMangaUpdateInfo> webMangaUpdateInfos;

//    @OrderBy(value = "status.読みたい.length desc")
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    @JsonManagedReference("manga-readStatus")
    private List<ReadStatus> readStatus;

    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    @JsonManagedReference("manga-tags")
    private List<MangaTag> mangaTags;


    @PrePersist
    public void onPrePersist() {
        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

}
