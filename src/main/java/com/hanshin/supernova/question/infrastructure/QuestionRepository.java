package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.Question;
import java.awt.print.Pageable;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByCommIdAndIsResolvedOrderByCreatedAtAsc(Long c_id, boolean isResolved);

    List<Question> findAllByCommIdAndIsResolvedOrderByCreatedAtDesc(Long c_id, boolean isResolved);

    List<Question> findAllByCommIdOrderByCreatedAtDesc(Long c_id);

    List<Question> findAllByCommIdOrderByCreatedAtAsc(Long c_id);

    List<Question> findTopNByIsResolvedOrderByCreatedAtDesc(boolean isResolved, Pageable pageable);
}