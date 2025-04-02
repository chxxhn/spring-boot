package com.example.mariadb_demo.notice;

import com.example.mariadb_demo.user.ApplicationUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Notice createNotice(NoticeDTO noticeDTO, ApplicationUser user) {
        Notice notice = new Notice(
                noticeDTO.getTitle(),
                noticeDTO.getContent(),
                user
        );
        return noticeRepository.save(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Page<Notice> getNoticePage(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return noticeRepository.findAll(pageable);
    }

    @Transactional
    public void update(Long noticeId, NoticeDTO noticeDTO) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지가 없습니다."));
        notice.setTitle(noticeDTO.getTitle());
        notice.setContent(noticeDTO.getContent());
    }
}
