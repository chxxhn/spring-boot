package com.example.mariadb_demo.FAQ;


import com.example.mariadb_demo.notice.Notice;
import com.example.mariadb_demo.notice.NoticeDTO;
import com.example.mariadb_demo.user.ApplicationUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FAQService {

    private final FAQRepository faqRepository;

    public void createFAQ(FAQDTO faqDTO, ApplicationUser user) {
        FAQ faq = new FAQ(
                faqDTO.getTitle(),
                faqDTO.getContent(),
                user
        );

        faqRepository.save(faq);
    }

    public Page<FAQ> getAllFAQs(Pageable pageable) {
        return faqRepository.findAll(pageable);
    }

    public FAQ findById(Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 FAQ가 없습니다. id=" + id));
    }

    @Transactional
    public void update(Long id, FAQDTO faqDTO) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ가 없습니다."));
        faq.setTitle(faqDTO.getTitle());
        faq.setContent(faqDTO.getContent());
    }

    @Transactional
    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}
