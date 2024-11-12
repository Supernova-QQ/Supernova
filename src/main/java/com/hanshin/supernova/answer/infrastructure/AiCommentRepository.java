package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.AiComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiCommentRepository extends JpaRepository<AiComment, Long> {

    AiComment findByQuestionId(Long questionId);
}
