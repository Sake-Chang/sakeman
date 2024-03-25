package com.sakeman.entity;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "review")
@EqualsAndHashCode(exclude = {"manga", "user"})
@ToString(exclude = {"manga", "user"})
@Where(clause = "delete_flag=0")
public class Review {

    /** フィールド */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "rating", nullable = false)
    @NotNull(message = "星をクリックしてお気に入り度を入力してください！")
    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(name = "title", nullable = false)
    @NotEmpty(message = "タイトルを入力してください！")
    private String title;

    @Column(name = "content")
    @Type(type = "text")
    @NotEmpty(message = "レビュー本文を入力してください！")
    private String content;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    /** 作品：manga_id */
    @ManyToOne
    @JoinColumn(name ="manga_id", referencedColumnName = "id")
    @NotNull
    private Manga manga;

    /** ユーザー：user_id */
    @ManyToOne
    @JoinColumn(name ="user_id", referencedColumnName = "id", nullable = true)
    @JsonBackReference
    private User user;

    /** いいね：like_id */
    @OneToMany(mappedBy = "review", cascade = CascadeType.MERGE)
    private List<Like> likes;


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
