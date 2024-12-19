package com.hanshin.supernova.bookmark.dto.response;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.bookmark.domain.BookmarkType;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private String title; // 질문 제목
    private String content; // 답변 내용
    private Long questionId; // 답변이 속한 질문 ID

    // 데이터가 제대로 직렬화되는지 확인
    @Override
    public String toString() {
        return "BookmarkResponse{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", questionId=" + questionId +
                '}';
    }

    public static BookmarkResponse fromEntity(Bookmark bookmark, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        if (bookmark.getType() == BookmarkType.QUESTION) {
            Question question = questionRepository.findById(bookmark.getTargetId())
                    .orElseThrow(() -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR));

            return new BookmarkResponse(
                    bookmark.getId(),
                    question.getTitle(),      // 실제 질문 제목
                    question.getContent(),    // 실제 질문 내용
                    bookmark.getTargetId()    // 북마크된 대상 ID
            );
        } else if (bookmark.getType() == BookmarkType.ANSWER) {
            Answer answer = answerRepository.findById(bookmark.getTargetId())
                    .orElseThrow(() -> new AnswerInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR));

            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR));

            return new BookmarkResponse(
                    bookmark.getId(),
                    question.getTitle(),      // 답변이 달린 질문의 제목
                    answer.getAnswer(),      // 실제 답변 내용
                    bookmark.getTargetId()    // 북마크된 대상 ID
            );
        }

        throw new IllegalArgumentException("Unknown BookmarkType: " + bookmark.getType());
    }

}
