package com.sakeman.entity;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
//@Setter
//@Getter
@Entity
@Table(name = "uclist_manga")
@EqualsAndHashCode(exclude = {"manga", "uclist"})
@ToString(exclude = {"manga", "uclist"})
public class UclistManga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** 作品：manga_id */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name ="manga_id", referencedColumnName = "id")
    private Manga manga;

    /** リスト：uclist_id */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "uclist_id", referencedColumnName = "id")
    private Uclist uclist;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;
    
    @Column(name = "delete_flag", nullable = false)
    private Integer deleteFlag;


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
