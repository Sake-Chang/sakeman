package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
import javax.persistence.OneToOne;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "user")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Where(clause = "delete_flag=0")
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    public static enum Role {
        管理者, 一般
    }
    public static enum VerificationTokenStatus {
        INVALID, VALID
    }


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @ToString.Include
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

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_status")
    @Enumerated(EnumType.STRING)
    private VerificationTokenStatus verificationTokenStatus;

    @Column(name = "verification_token_expiration")
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


    /** 関連エンティティ */
    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-badges")
    private List<BadgeUser> badgeusers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-reviews")
    private List<Review> reviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-uclists")
    private List<Uclist> uclists;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-likes")
    private List<Like> Likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-webLikes")
    private List<WebLike> webLikes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-webMangaFollows")
    private List<WebMangaFollow> webMangaFollows;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-authors")
    private List<AuthorFollow> authorFollows;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.MERGE) // このユーザーがフォローしている人
    @JsonManagedReference("user-followers")
    private List<UserFollow> followings;

    @OneToMany(mappedBy = "followee", cascade = CascadeType.MERGE) // このユーザーをフォローしている人
    @JsonManagedReference("user-followees")
    private List<UserFollow> followers;

    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
    @JsonManagedReference("user-readStatus")
    private List<ReadStatus> readStatus;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_web_manga_setting_id", referencedColumnName = "id")
    private UserWebMangaSetting userWebMangaSetting;

//    @ElementCollection(targetClass=Integer.class)
//    @CollectionTable(name = "user_web_manga_settings_genre")
//    @Column(name = "web_manga_settings_genre", nullable = false)
//    private List<Integer> webMangaSettingsGenre = new ArrayList<>();
//
//    @Column(name = "web_manga_settings_freeflag", nullable = false)
//    private Integer webMangaSettingsFreeflag = 0;
//
//    @Column(name = "web_manga_settings_followflag", nullable = false)
//    private Integer webMangaSettingsFollowflag = 0;
//
//    @Column(name = "web_manga_settings_oneshotflag", nullable = false)
//    private Integer webMangaSettingsOneshotflag = 0;

    @PrePersist
    public void onPrePersist() {
        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        if (this.userWebMangaSetting == null) {
            UserWebMangaSetting settings = new UserWebMangaSetting();

            settings.setFreeflagSetting(0);  // 必要であれば明示的に設定
            settings.setFollowflagSetting(0);
            settings.setOneshotflagSetting(0);
            settings.setGenreSettings(new ArrayList<>());

            this.userWebMangaSetting = settings;
        }
    }

    @PreUpdate
    public void onPreUpdate() {
        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }

}

