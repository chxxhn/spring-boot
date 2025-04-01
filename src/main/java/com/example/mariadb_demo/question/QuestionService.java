package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public Question createQuestion(QuestionDTO questionDTO, ApplicationUser user) {
        Question question = new Question(
                questionDTO.getTitle(),
                questionDTO.getContent(),
                questionDTO.isSecret(),
                user
        );

        return questionRepository.save(question);
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

    @Transactional
    public Question increaseViewAndGet(Long questionId) {
        questionRepository.increaseView(questionId);
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));
    }

    public Page<Question> getQuestionPage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return questionRepository.findAll(pageable);
    }

}
