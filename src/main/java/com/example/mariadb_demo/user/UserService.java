package com.example.mariadb_demo.user;

import com.example.mariadb_demo.exception.CustomExceptions;
import com.example.mariadb_demo.notice.Notice;
import com.example.mariadb_demo.user.login.OAuthProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public void deleteUserById(Long id) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getRole() == UserRole.ADMIN) {
            throw new IllegalStateException("관리자 계정은 삭제할 수 없습니다.");
        }
        userRepository.deleteById(id);
    }

    public void changeUserStatus(Long id, boolean status) {
        ApplicationUser user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        if (user.getRole() == UserRole.ADMIN) {
            throw new CustomExceptions.AdminModificationNotAllowedException("관리자는 상태 변경할 수 없습니다.");
        }

        user.setEnabled(status);
        userRepository.save(user);
    }

    private Specification<ApplicationUser> search(String kw) {
        return (Root<ApplicationUser> n, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            query.distinct(true);

            if (kw == null || kw.trim().isEmpty()) {
                return cb.conjunction();
            }

            return cb.or(
                    cb.like(n.get("username"), "%" + kw + "%"),
                    cb.like(n.get("phone"), "%" + kw + "%")
            );
        };
    }

    public Page<ApplicationUser> getSearchResult(String kw, Pageable pageable) {
        Specification<ApplicationUser> spec = search(kw);
        return userRepository.findAll(spec, pageable);
    }

}
