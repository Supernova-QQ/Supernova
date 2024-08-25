package com.hanshin.supernova.question.infrastructure;

import com.hanshin.supernova.question.domain.Question;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findAllByCommIdAndIsResolved(Long c_id, boolean isResolved);

    List<Question> findAllByCommId(Long c_id);
}
