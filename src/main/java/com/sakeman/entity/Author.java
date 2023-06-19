package com.sakeman.entity;

import java.sql.Timestamp;
import java.util.Set;
import java.util.List;


import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "author")
@ToString(exclude = {"mangaAuthors"})
public class Author {

    /** フィールド */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "name_kana")
    private String nameKana;

    @Column(name = "profile")
    @Type(type = "text")
    private String profile;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    @Column(name = "display_flag")
    private Integer displayFlag;

    @Column(name = "img", nullable = true)
    private String img;

    /** manga_author */
    @OneToMany(mappedBy = "author", cascade = CascadeType.MERGE)
    private Set<MangaAuthor> mangaAuthors;



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
