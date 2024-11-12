package com.sakeman.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "user_web_manga_setting")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Where(clause = "delete_flag=0")
public class UserWebMangaSetting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    @OneToMany(mappedBy = "userWebMangaSetting", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference("userWebMangaSetting-genres")
    private List<UserWebMangaSettingGenre> webMangaSettingsGenres = new ArrayList<>();

    @Column(name = "web_manga_settings_freeflag", nullable = false)
    private Integer webMangaSettingsFreeflag = 0;

    @Column(name = "web_manga_settings_followflag", nullable = false)
    private Integer webMangaSettingsFollowflag = 0;

    @Column(name = "web_manga_settings_oneshotflag", nullable = false)
    private Integer webMangaSettingsOneshotflag = 0;

    @Column(name = "delete_flag", nullable = false)
    private boolean deleteFlag = false;
}
