package com.sakeman.entity;
import java.io.Serializable;
import java.time.LocalDateTime;
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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "web_manga_update_info")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class WebMangaUpdateInfo implements Serializable, Comparable<WebMangaUpdateInfo> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Integer id;

    private String mediaName;
    private String titleString;
    private String subTitle;
    private String authorString;
    private String url;
    private String imgUrl;
    private Integer freeFlag;
    private LocalDateTime updateAt;

    /** 関連エンティティ */
    @ManyToOne
    @JoinColumn(name ="manga_id", referencedColumnName = "id")
    @JsonBackReference("manga-webMangaUpdateInfos")
    private Manga manga;

    @ManyToOne
    @JoinColumn(name ="webMangaMedia_id", referencedColumnName = "id")
    @JsonBackReference("media-webMangaUpdateInfos")
    @Where(clause = "delete_flag=0")
    private WebMangaMedia webMangaMedia;

    @OneToMany(mappedBy = "webMangaUpdateInfo", cascade = CascadeType.MERGE)
    @JsonManagedReference("webMangaUpdateInfo-webLikes")
    private List<WebLike> webLikes;


    @Override
    public int compareTo(WebMangaUpdateInfo o) {
        if(this.updateAt.isAfter(o.getUpdateAt())) {
            return -1;
        } else if (this.updateAt.isBefore(o.getUpdateAt())) {
            return 1;
        } else {
            return 0;
        }
    }

}
