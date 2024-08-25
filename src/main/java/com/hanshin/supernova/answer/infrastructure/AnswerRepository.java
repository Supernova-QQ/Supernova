package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.Answer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAllByQuestionId(Long qId);
}
