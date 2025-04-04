package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.UserRole;
import com.example.mariadb_demo.user.login.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;
    private final CommentService commentService;

    public QuestionController(QuestionService questionService, CommentService commentService) {
        this.questionService = questionService;
        this.commentService = commentService;
    }

    @GetMapping("")
    public String showQuestionPage(@RequestParam(value = "page", defaultValue = "0") int page, Model model,
                                   @RequestParam(value = "kw", defaultValue = "") String kw) {
        page = Math.max(page, 0);
        List<Question> questions = questionService.getAllQuestions();
        Map<Long, Integer> commentCounts = new HashMap<>();
        Page<Question> questionPage = questionService.getSearchResult(kw, PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        // n+1 방지
        for (Question q : questions) {
            int count = commentService.countByQuestionId(q.getId());
            commentCounts.put(q.getId(), count);
        }

        model.addAttribute("commentCounts", commentCounts);
        model.addAttribute("questionPage", questionPage);
        model.addAttribute("kw", kw);

        return "question/list";
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String showAddQuestionPage(Model model) {
        model.addAttribute("questionDTO", new QuestionDTO());
        return "question/add";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String addQuestion(@ModelAttribute QuestionDTO questionDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            throw new IllegalStateException("로그인된 사용자 정보가 없습니다.");
        }
        ApplicationUser user = userDetails.getUser();
        questionService.createQuestion(questionDTO, user);
        return "redirect:/question";
    }

    @GetMapping("/edit/{questionId}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long questionId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Question question = questionService.findById(questionId);
        if (!question.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        model.addAttribute("question", question);
        return "question/edit";
    }

    @PostMapping("/edit/{questionId}")
    @PreAuthorize("isAuthenticated()")
    public String editQuestion(@PathVariable Long questionId,
                               @ModelAttribute QuestionDTO questionDTO,
                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Question question = questionService.findById(questionId);
        if (!question.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        questionService.update(questionId, questionDTO);
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/delete/{questionId}")
    public String deleteQuestion(@PathVariable Long questionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Question question = questionService.findById(questionId);
        boolean isWriter = question.getUser().getId().equals(userDetails.getUser().getId());
        boolean isAdmin = userDetails.getUser().getRole().name().equals("ADMIN");
        if (!isWriter && !isAdmin) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        questionService.delete(questionId);
        return "redirect:/question";
    }

    @GetMapping("/{questionId}")
    @Transactional
    public String showQuestionDetailPage(@PathVariable Long questionId, Model model,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Question question = questionService.findById(questionId);
        List<Comment> comments = commentService.getComments(questionId);
        if (question.isSecret()) {
            boolean isWriter = userDetails != null && question.getUser().getId().equals(userDetails.getUser().getId());
            boolean isAdmin = userDetails != null && userDetails.getUser().getRole().equals(UserRole.ADMIN);

            if (!isWriter && !isAdmin) {
                model.addAttribute("secretBlocked", true);
            }
        }
        model.addAttribute("question", question);
        model.addAttribute("comments", comments);
        model.addAttribute("commentDTO", new CommentDTO());
        return "question/detail";
    }

}