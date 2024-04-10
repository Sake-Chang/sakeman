package com.sakeman.entity;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
//@Setter
//@Getter
@Entity
@Table(name = "read_status")
@EqualsAndHashCode(exclude = {"user", "manga"})
@ToString(exclude = {"user", "manga"})
public class ReadStatus {

    public static enum Status {
        未登録, 気になる, 読んだ
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "read_status")
    @Enumerated(EnumType.STRING)
    private Status status;

    /** ユーザー：user_id */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name ="user_id", referencedColumnName = "id")
    @JsonBackReference("user-readStatus")
    private User user;

    /** 作品：manga_id */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "manga_id", referencedColumnName = "id")
    private Manga manga;

    /**　押された場所 */
    @Column(name = "push_at")
    private String pushAt;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    private Timestamp updatedAt;


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
