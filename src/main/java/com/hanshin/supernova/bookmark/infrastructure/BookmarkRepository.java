package com.hanshin.supernova.bookmark.infrastructure;

import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.answer.domain.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    // 특정 사용자가 북마크한 모든 질문 목록 조회
    List<Bookmark> findAllByUserAndQuestionIsNotNull(User user);

    // 특정 사용자가 북마크한 모든 답변 목록 조회
    List<Bookmark> findAllByUserAndAnswerIsNotNull(User user);

    // 특정 사용자가 특정 질문을 북마크했는지 확인
    Optional<Bookmark> findByUserAndQuestion(User user, Question question);

    // 특정 사용자가 특정 답변을 북마크했는지 확인
    Optional<Bookmark> findByUserAndAnswer(User user, Answer answer);

    // 특정 사용자의 질문 북마크 삭제
    @Transactional
    void deleteByUserAndQuestion(User user, Question question);

    // 특정 사용자의 답변 북마크 삭제
    @Transactional
    void deleteByUserAndAnswer(User user, Answer answer);
}
