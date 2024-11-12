package com.hanshin.supernova.hashtag.infrastructure;

import com.hanshin.supernova.hashtag.domain.QuestionHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface QuestionHashtagRepository extends JpaRepository<QuestionHashtag, Long> {

    void deleteAllByQuestionId(Long questionId);

    List<QuestionHashtag> findByQuestionId(Long qId);
    List<QuestionHashtag> findByHashtagId(Long hId);
}
