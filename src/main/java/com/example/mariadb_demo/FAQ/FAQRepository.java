package com.example.mariadb_demo.FAQ;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long>, JpaSpecificationExecutor<FAQ> {
    Page<FAQ> findAll(Pageable pageable);
}
