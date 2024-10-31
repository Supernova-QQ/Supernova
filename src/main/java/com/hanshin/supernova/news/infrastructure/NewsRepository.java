package com.hanshin.supernova.news.infrastructure;

import com.hanshin.supernova.news.domain.News;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface NewsRepository extends JpaRepository<News, Long> {

    Page<News> findAllByReceiverIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<News> findAllByReceiverIdOrderByCreatedAtDesc(Long userId);

    List<News> findAllByReceiverIdAndIsViewedFalseOrderByCreatedAtDesc(Long userId);

    List<News> findAllByReceiverIdAndIsViewedTrueOrderByCreatedAtDesc(Long userId);
}
