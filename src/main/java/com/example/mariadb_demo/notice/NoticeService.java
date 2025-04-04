package com.example.mariadb_demo.notice;

import com.example.mariadb_demo.question.Comment;
import com.example.mariadb_demo.question.Question;
import com.example.mariadb_demo.user.ApplicationUser;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice findById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지가 없습니다. id=" + noticeId));
    }

    public void createNotice(NoticeDTO noticeDTO, ApplicationUser user) {
        Notice notice = new Notice(
                noticeDTO.getTitle(),
                noticeDTO.getContent(),
                user
        );
        noticeRepository.save(notice);
    }


    @Transactional
    public void update(Long noticeId, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지가 없습니다."));
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
    }


    @Transactional
    public void delete(Long noticeId) {
        noticeRepository.deleteById(noticeId);
    }


    private Specification<Notice> search(String kw) {
        return (Root<Notice> n, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            if (kw == null || kw.trim().isEmpty()) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.like(n.get("title"), "%" + kw + "%"),
                    cb.like(n.get("content"), "%" + kw + "%")
            );
        };
    }


    public Page<Notice> getSearchResult(String kw, Pageable pageable) {
        Specification<Notice> spec = search(kw);
        return noticeRepository.findAll(spec, pageable);
    }


    public Page<Notice> getNoticesWithSorting(int page, String kw) {
        Sort sort = Sort.by(
                Sort.Order.desc("isImportant"),
                Sort.Order.desc("priority"),
                Sort.Order.desc("createdAt")
        );
        Pageable pageable = PageRequest.of(page, 10, sort);
        return getSearchResult(kw, pageable);
    }
}