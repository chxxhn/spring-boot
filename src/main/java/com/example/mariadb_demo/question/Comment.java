package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    public Comment(String content, Question question, ApplicationUser user) {
        this.content = content;
        this.question = question;
        this.user = user;
    }

    protected Comment() {
    }

}