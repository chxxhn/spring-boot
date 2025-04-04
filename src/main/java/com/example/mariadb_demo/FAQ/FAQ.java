package com.example.mariadb_demo.FAQ;


import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class FAQ extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAQ_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private ApplicationUser user;

    public FAQ(String title, String content, ApplicationUser user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public FAQ() {

    }
}
