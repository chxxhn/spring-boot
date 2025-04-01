package com.example.mariadb_demo.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByQuestionIdOrderByUpdatedAtDesc(Long questionId);
    int countByQuestionId(Long questionId);
}

