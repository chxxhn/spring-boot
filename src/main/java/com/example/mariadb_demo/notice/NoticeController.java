package com.example.mariadb_demo.notice;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    @GetMapping("")
    public String showNoticePage() {
        return "notice/list";
    }

    @GetMapping("/add")
    public String showAddNoticePage(Model model) {
        model.addAttribute("noticeDTO", new NoticeDTO());
        return "notice/add";
    }
}
