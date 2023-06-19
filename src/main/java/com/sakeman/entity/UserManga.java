package com.sakeman.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 使ってない */

//
//@Data
////@Setter
////@Getter
//@Entity
//@Table(name = "user_manga")
//@EqualsAndHashCode(exclude = {"manga", "user"})
//@ToString(exclude = {"manga", "user"})
//@NoArgsConstructor
//public class UserManga implements Serializable {
//    
//    private static final long serialVersionUID = 1L;
//
//    public static enum Status {
//        未登録, 気になる, 読んだ
//    }
//
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    @Column(name = "id")
////    private Integer id;
//
//    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    private Status status;
//
//    @EmbeddedId
//    private UserMangaPK pkey;
//    
//    /** ユーザー：user_id */
//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable=false,  updatable=false)
//    private User user;
//    
//    /** 作品：manga_id */
//    @ManyToOne(cascade = CascadeType.MERGE)
//    @JoinColumn(name = "manga_id", referencedColumnName = "id", insertable=false,  updatable=false)
//    private Manga manga;
//    
//    @Column(name = "registered_at", nullable = false, updatable = false)
//    @CreatedDate
//    private Timestamp registeredAt;
//
//    @Column(name = "updated_at", nullable = false)
//    @LastModifiedDate
//    private Timestamp updatedAt;
//
//
//    @PrePersist
//    public void onPrePersist() {
//        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
//        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//    }
//
//    @PreUpdate
//    public void onPreUpdate() {
//        setUpdatedAt(new Timestamp(System.currentTimeMillis()));
//    }
//
//}
