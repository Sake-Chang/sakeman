package com.sakeman.entity;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User implements Serializable {

    public static enum Role {
        管理者, 一般
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "username", nullable = false)
    @NotBlank(message = "必須項目です。")
    @Size(min = 1, max = 50, message = "1~50文字でお願いします！")
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank(message = "必須項目です。")
    @Email(message = "ちゃんとしたメールアドレスをお願いします！")
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank(message = "空白はNGです！")
    private String password;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;

    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;

    @Column(name = "self_intro", nullable = true)
    @Type(type = "text")
    private String selfIntro;

    @Column(name = "img", nullable = true)
    private String img;

    /** review */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Review> reviews;

    /** uclist */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Uclist> uclists;

//    /** user_manga（未使用） */
//    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
//    private List<UserManga> userMangas;

    /** like */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<Like> Likes;

    /** webLike */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<WebLike> webLikes;

    /** readStatus */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    private List<ReadStatus> readStatus;


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

