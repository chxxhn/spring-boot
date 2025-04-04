package com.example.mariadb_demo.FAQ;


import com.example.mariadb_demo.notice.Notice;
import com.example.mariadb_demo.notice.NoticeDTO;
import com.example.mariadb_demo.user.ApplicationUser;
import com.example.mariadb_demo.user.login.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/FAQ")
public class FAQController {

    private final FAQService faqService;

    public FAQController(FAQService faqService) {
        this.faqService = faqService;
    }

    @GetMapping("")
    public String showFAQPage(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        Page<FAQ> faqPage = faqService.getAllFAQs(PageRequest.of(page, 10));
        model.addAttribute("faqPage", faqPage);
        return "FAQ/list";
    }

    @GetMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String showAddFAQPage(Model model) {
        model.addAttribute("faqDTO", new FAQDTO());
        return "FAQ/add";
    }

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public String addFAQ(@ModelAttribute FAQDTO faqDTO, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("등록 권한이 없습니다.");
        }
        ApplicationUser user = userDetails.getUser();
        faqService.createFAQ(faqDTO, user);
        return "redirect:/FAQ";
    }

    @GetMapping("/edit/{faqId}")
    @PreAuthorize("isAuthenticated()")
    public String showEditForm(@PathVariable Long faqId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        FAQ faq = faqService.findById(faqId);
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        model.addAttribute("faq", faq);
        return "FAQ/edit";
    }

    @PostMapping("/edit/{faqId}")
    @PreAuthorize("isAuthenticated()")
    public String editFAQ(@PathVariable Long faqId,
                          @ModelAttribute FAQDTO faqDTO,
                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        faqService.update(faqId, faqDTO);
        return "redirect:/FAQ";
    }

    @PostMapping("/delete/{faqId}")
    public String deleteNotice(@PathVariable Long faqId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (!userDetails.getUser().getRole().name().equals("ADMIN")) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }
        faqService.delete(faqId);
        return "redirect:/FAQ";
    }

}
