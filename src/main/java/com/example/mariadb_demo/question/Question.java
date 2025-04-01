package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.BaseEntity;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Question extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean secret;

    @Column(nullable = false)
    private int views = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    @OneToMany(mappedBy = "question")
    private List<Comment> comments = new ArrayList<>();

    public Question(String title, String content, boolean secret, ApplicationUser user) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.secret = secret;
    }

    public Question() {

    }
}
