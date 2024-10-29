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

@SuppressWarnings("serial")
@Data
@Entity
@Table(name = "user")
@ToString(exclude = {"uclists", "Likes", "webLikes", "readStatus", "badgeusers", "webMangaSettingsGenre", "webMangaSettingsFollowflag", "webMangaFollows"})
@JsonIgnoreProperties({"uclists", "Likes", "webLikes", "readStatus", "badgeusers", "webMangaSettingsGenre", "webMangaSettingsFollowflag", "password", "role", "registeredAt", "updatedAt", "deleteFlag", "selfIntro", "img", "webMangaFollows"})
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

    @Column(name = "new_email")
    private String newEmail;

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
    @Size(max = 500, message = "500文字以下でお願いします！")
    private String selfIntro;

    @Column(name = "img", nullable = true)
    private String img;

    /** review */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-reviews")
    private List<Review> reviews;

    /** uclist */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-uclist")
    private List<Uclist> uclists;

    /** like */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-likes")
    private List<Like> Likes;

    /** webLike */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-webLike")
    private List<WebLike> webLikes;

    /** readStatus */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-readStatus")
    private List<ReadStatus> readStatus;

    /** badge_user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-badgeuser")
    private List<BadgeUser> badgeusers;

    /** badge_user */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-webMangaFollow")
    private List<WebMangaFollow> webMangaFollows;

    // ユーザーがフォローしている人のリスト
    @OneToMany(mappedBy = "follower")
    @JsonManagedReference("user-follower")
    private List<UserFollow> followings;

    // ユーザーをフォローしている人のリスト
    @OneToMany(mappedBy = "followee")
    @JsonManagedReference("user-followee")
    private List<UserFollow> followers;


    @ElementCollection(targetClass=Integer.class)
    @CollectionTable(name = "user_web_manga_settings_genre")
    @Column(name = "web_manga_settings_genre", nullable = false)
    private List<Integer> webMangaSettingsGenre = new ArrayList<>();

    @Column(name = "web_manga_settings_freeflag", nullable = false)
    private Integer webMangaSettingsFreeflag = 0;
    @Column(name = "web_manga_settings_followflag", nullable = false)
    private Integer webMangaSettingsFollowflag = 0;


    @PrePersist
    public void onPrePersist() {
        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        if (this.webMangaSettingsGenre == null) {
            this.webMangaSettingsGenre = new ArrayList<>();
        }
        if (this.webMangaSettingsFreeflag == null) {
            this.webMangaSettingsFreeflag = 0;
        }
        if (this.webMangaSettingsFollowflag == null) {
            this.webMangaSettingsFollowflag = 0;
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

}

