package com.sakeman.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 使ってない */

//@Embeddable
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserMangaPK implements Serializable {
//
//    private static final long serialVersionUID = 1L;
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
//}
