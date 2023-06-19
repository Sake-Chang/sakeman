package com.sakeman.entity;

import java.sql.Timestamp;
import java.util.List;
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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "manga")
@ToString(exclude = {"reviews", "mangaAuthors", "uclistMangas"})
public class Manga {

    /** フィールド */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

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

    /** review */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.ALL)
    private List<Review> reviews;

    /** manga_author */
    @OneToMany(mappedBy = "manga", cascade = CascadeType.MERGE)
    private List<MangaAuthor> mangaAuthors;

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

}
