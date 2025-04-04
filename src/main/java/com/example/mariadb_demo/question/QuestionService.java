package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public void createQuestion(QuestionDTO questionDTO, ApplicationUser user) {
        Question question = new Question(
                questionDTO.getTitle(),
                questionDTO.getContent(),
                questionDTO.isSecret(),
                user
        );

        questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question findById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 질문이 없습니다. id=" + id));
    }

    @Transactional
    public void update(Long id, QuestionDTO dto) {
        Question q = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("질문이 없습니다."));
        q.setTitle(dto.getTitle());
        q.setContent(dto.getContent());
        q.setSecret(dto.isSecret());
    }


    @Transactional
    public void delete(Long id) {
        questionRepository.deleteById(id);
    }


    private Specification<Question> search(String kw) {
        return (Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            if (kw == null || kw.trim().isEmpty()) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.like(q.get("title"), "%" + kw + "%"),
                    cb.like(q.get("content"), "%" + kw + "%")
            );
        };
    }


    public Page<Question> getSearchResult(String kw, Pageable pageable) {
        Specification<Question> spec = search(kw);
        return questionRepository.findAll(spec, pageable);
    }


}