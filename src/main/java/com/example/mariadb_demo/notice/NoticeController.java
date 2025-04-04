package com.example.mariadb_demo.notice;

import com.example.mariadb_demo.question.Question;
import com.example.mariadb_demo.question.QuestionDTO;
import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.login.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping("")
    public String showNoticePage(@RequestParam(value = "page", defaultValue = "0") int page, Model model,
                                 @RequestParam(value = "kw", defaultValue = "") String kw) {
        page = Math.max(page, 0);
        Page<Notice> noticePage = noticeService.getNoticesWithSorting(page, kw);
        model.addAttribute("noticePage", noticePage);
        model.addAttribute("kw", kw);
        return "notice/list";
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String showAddNoticePage(Model model) {
        model.addAttribute("noticeDTO", new NoticeDTO());
        return "notice/add";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String addNotice(@ModelAttribute NoticeDTO noticeDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("등록 권한이 없습니다.");
        }
        ApplicationUser user = userDetails.getUser();
        noticeService.createNotice(noticeDTO, user);
        return "redirect:/notice";
    }

    @GetMapping("/{noticeId}")
    public String showNoticeDetailPage(@PathVariable Long noticeId, Model model) {
        Notice notice = noticeService.findById(noticeId);
        model.addAttribute("notice", notice);
        return "notice/detail";
    }

    @GetMapping("/edit/{noticeId}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long noticeId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Notice notice = noticeService.findById(noticeId);
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        model.addAttribute("notice", notice);
        return "notice/edit";
    }

    @PostMapping("/edit/{noticeId}")
    @PreAuthorize("isAuthenticated()")
    public String editNotice(@PathVariable Long noticeId,
                             @ModelAttribute NoticeDTO noticeDTO,
                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        noticeService.update(noticeId, noticeDTO);
        return "redirect:/notice/" + noticeId;
    }

    @PostMapping("/delete/{noticeId}")
    public String deleteNotice(@PathVariable Long noticeId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        noticeService.delete(noticeId);
        return "redirect:/notice";
    }

}