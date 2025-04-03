package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final QuestionRepository questionRepository;

    public void addComment(Long questionId, String content, ApplicationUser user) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문 없음"));
        Comment comment = new Comment(content, question, user);
        commentRepository.save(comment);
    }

    @Transactional
    public void update(Long id, String content) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("댓글이 없습니다."));
        comment.setContent(content);
    }

    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    public List<Comment> getComments(Long questionId) {
        return commentRepository.findByQuestionIdOrderByUpdatedAtDesc(questionId);
    }

    public Comment findById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다. id=" + id));
    }

    public int countByQuestionId(Long questionId) {
        return commentRepository.countByQuestionId(questionId);
    }
}
