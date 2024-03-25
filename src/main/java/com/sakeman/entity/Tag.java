package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

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
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "tag")
@EqualsAndHashCode(exclude = {"manga", "user"})
@ToString(exclude = {"manga", "user"})
@JsonIgnoreProperties({"registeredAt", "updatedAt", "mangaTags", "genreTags"})
public class Tag implements Serializable {

    /** フィールド */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tagname", nullable = false)
    private String tagname;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;

    /** manga_tag */
    @OneToMany(mappedBy = "tag", cascade = CascadeType.MERGE)
    private List<MangaTag> mangaTags;

//    /** ユーザー：user_id */
//    @ManyToOne
//    @JoinColumn(name ="user_id", referencedColumnName = "id", nullable = true)
//    private User user;

    /** genre_tag */
    @OneToMany(mappedBy = "tag", cascade = CascadeType.MERGE)
    private List<GenreTag> genreTags;

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
