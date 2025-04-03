package com.example.mariadb_demo.question;

import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.login.CustomUserDetails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/add/{questionId}")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@PathVariable Long questionId,
                             @ModelAttribute CommentDTO commentDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null || userDetails.getUser() == null) {
            throw new IllegalStateException("로그인된 사용자 정보가 없습니다.");
        }
        commentService.addComment(questionId, commentDTO.getContent(), userDetails.getUser());
        return "redirect:/question/" + questionId;
    }

    @PostMapping("/edit/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public String editComment(@PathVariable Long commentId,
                              @RequestParam("content") String content,
                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = commentService.findById(commentId);
        if (!comment.getUser().getId().equals(userDetails.getUser().getId())) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        commentService.update(commentId, content);
        return "redirect:/question/" + comment.getQuestion().getId();
    }

    @PostMapping("/delete/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable Long commentId,
                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Comment comment = commentService.findById(commentId);
        if (!comment.getUser().getId().equals(userDetails.getUser().getId()) &&
                !userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }
        Long questionId = comment.getQuestion().getId();
        commentService.delete(commentId);

        return "redirect:/question/" + questionId;
    }



}