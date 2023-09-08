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

import lombok.Data;

@Data
@Entity
@Table(name = "web_manga_update_info")
public class WebMangaUpdateInfo implements Serializable, Comparable<WebMangaUpdateInfo> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private String mediaName;
    private String titleString;
    private String subTitle;
    private String authorString;
    private String url;
    private String imgUrl;

    private Integer freeFlag;

    private LocalDateTime updateAt;

    /** 作品：manga_id */
    @ManyToOne
    @JoinColumn(name ="manga_id", referencedColumnName = "id")
    private Manga manga;

    /** メディア */
    @ManyToOne
    @JoinColumn(name ="webMangaMedia_id", referencedColumnName = "id")
    private WebMangaMedia webMangaMedia;

    /** いいね：weblike_id */
    @OneToMany(mappedBy = "webMangaUpdateInfo", cascade = CascadeType.MERGE)
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
