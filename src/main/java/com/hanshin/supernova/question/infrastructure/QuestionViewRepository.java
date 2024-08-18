package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.QuestionView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface QuestionViewRepository extends JpaRepository<QuestionView, Long> {

    boolean existsByViewerId(Long viewerId);
    QuestionView findByViewerId(Long viewerId);
}
