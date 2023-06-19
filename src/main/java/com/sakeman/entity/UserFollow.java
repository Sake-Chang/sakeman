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
@Table(name = "user_follow")
@EqualsAndHashCode(exclude = "user")
@ToString(exclude = "user")
public class UserFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /** フォロワー（フォローした人） */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name ="follower_user_id", referencedColumnName = "id")
    private User follower;

    /** フォロイー（フォローされた人） */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "followee_user_id", referencedColumnName = "id")
    private User followee;

    @Column(name = "registered_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp registeredAt;

    @PrePersist
    public void onPrePersist() {
        setRegisteredAt(new Timestamp(System.currentTimeMillis()));
    }

    @PreUpdate
    public void onPreUpdate() {
    }

}
