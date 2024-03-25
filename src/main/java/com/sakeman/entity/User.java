package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.JoinColumn;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;

@Data
@Entity
@Table(name = "user")
@ToString(exclude = {"reviews", "uclists", "Likes", "webLikes", "readStatus", "badgeusers", "webMangaSettingsGenre", "webMangaSettingsFollowflag", "webMangaFollows"})
@JsonIgnoreProperties({"reviews", "uclists", "Likes", "webLikes", "readStatus", "badgeusers", "webMangaSettingsGenre", "webMangaSettingsFollowflag", "password", "role", "registeredAt", "updatedAt", "deleteFlag", "selfIntro", "img", "webMangaFollows"})
@Where(clause = "delete_flag=0")
public class User implements Serializable {

    public static enum Role {
        管理者, 一般
    }
    public static enum VerificationTokenStatus {
        INVALID, VALID
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

    private boolean isEnabled;

    private String verificationToken;

    @Enumerated(EnumType.STRING)
    private VerificationTokenStatus verificationTokenStatus;

    private Date verificationTokenExpiration;

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
    @JsonManagedReference
    private List<Review> reviews;

    /** uclist */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Uclist> uclists;

    /** like */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<Like> Likes;

    /** webLike */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<WebLike> webLikes;

    /** readStatus */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<ReadStatus> readStatus;

    /** badge_user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<BadgeUser> badgeusers;

    /** badge_user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference
    private List<WebMangaFollow> webMangaFollows;

    // 配列バージョン
//    @Column(name = "web_manga_settings_genre", nullable = true)
//    private int[] webMangaSettingsGenre;

//    @ElementCollection(targetClass=Integer.class, fetch=FetchType.EAGER)
    @ElementCollection(targetClass=Integer.class)
    @CollectionTable(name = "user_web_manga_settings_genre")
    @Column(name = "web_manga_settings_genre", nullable = true)
    private List<Integer> webMangaSettingsGenre = new ArrayList<>();

    @Column(name = "web_manga_settings_freeflag", nullable = true)
    private Integer webMangaSettingsFreeflag;
    @Column(name = "web_manga_settings_followflag", nullable = true)
    private Integer webMangaSettingsFollowflag;


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

