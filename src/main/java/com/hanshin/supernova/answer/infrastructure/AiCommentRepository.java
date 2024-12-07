package com.hanshin.supernova.answer.infrastructure;

import com.hanshin.supernova.answer.domain.AiComment;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiCommentRepository extends JpaRepository<AiComment, Long> {

    Optional<AiComment> findByQuestionId(Long questionId);
}
