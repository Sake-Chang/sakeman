package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@Table(name = "web_manga_media")
@ToString(exclude = {"webMangaUpdateInfos"})
@Where(clause = "delete_flag=0")
public class WebMangaMedia implements Serializable {

    /** フィールド */

    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "display_name", nullable = false)
    private String displayName;

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

    @Column(name = "logo", nullable = true)
    private String logo;

    @Column(name = "rss")
    private String rss;

    @Column(name= "recent_update_at")
    private LocalDateTime recentUpdateAt;


    /** webMangaUpdateInfo */
    @OneToMany(mappedBy = "webMangaMedia", cascade = CascadeType.ALL)
    private List<WebMangaUpdateInfo> webMangaUpdateInfos;

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
