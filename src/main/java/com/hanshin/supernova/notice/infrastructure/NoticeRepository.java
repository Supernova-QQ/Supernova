package com.hanshin.supernova.notice.infrastructure;

import com.hanshin.supernova.notice.domain.Notice;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findAllByOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notice n WHERE n.isPinned = TRUE ORDER BY n.createdAt DESC")
    List<Notice> findPinnedNotices();
}
