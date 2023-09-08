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
import javax.persistence.OneToOne;
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
@Entity
//@Table(name = "likes")
@EqualsAndHashCode(exclude = {"manga"})
@ToString(exclude = {"manga"})
public class WebMangaTitleConverter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "titleString")
    private String titleString;

    @Column(name = "authorString")
    private String authorString;

    /** マンガ：manga_id */
    @OneToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name ="manga_id", referencedColumnName = "id")
    private Manga manga;

}
