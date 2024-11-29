package com.hanshin.supernova.bookmark.application;

import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.bookmark.domain.BookmarkType;
import com.hanshin.supernova.bookmark.dto.request.BookmarkRequest;
import com.hanshin.supernova.bookmark.dto.response.BookmarkResponse;
import com.hanshin.supernova.bookmark.infrastructure.BookmarkRepository;
import com.hanshin.supernova.exception.bookmark.BookmarkNotFoundException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    /**
     * 특정 유저의 질문 북마크 리스트 조회
     */
    public List<BookmarkResponse> getBookmarkedQuestions(Long userId, Long commId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdAndCommIdAndType(userId, commId, BookmarkType.QUESTION);

        if (bookmarks == null || bookmarks.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }

        // 람다식을 사용하여 추가 매개변수 전달
        return bookmarks.stream()
                .map(bookmark -> BookmarkResponse.fromEntity(bookmark, questionRepository, answerRepository))
                .collect(Collectors.toList());
    }

    /**
     * 특정 유저의 답변 북마크 리스트 조회
     */
    public List<BookmarkResponse> getBookmarkedAnswers(Long userId, Long commId) {
        List<Bookmark> bookmarks = bookmarkRepository.findByUserIdAndCommIdAndType(userId, commId, BookmarkType.ANSWER);

        if (bookmarks == null || bookmarks.isEmpty()) {
            return Collections.emptyList(); // 빈 리스트 반환
        }

        // 람다식을 사용하여 추가 매개변수 전달
        return bookmarks.stream()
                .map(bookmark -> BookmarkResponse.fromEntity(bookmark, questionRepository, answerRepository))
                .collect(Collectors.toList());
    }

    /**
     * 북마크 추가
     */
    public void addBookmark(Long userId, BookmarkRequest request) {
        Bookmark bookmark = Bookmark.builder()
                .userId(userId)
                .targetId(request.getTargetId())
                .type(request.getType())
                .commId(request.getCommId())
                .build();
        bookmarkRepository.save(bookmark);
    }

    /**
     * 북마크 삭제
     */
    public void removeBookmark(Long userId, BookmarkRequest request) {
        BookmarkType bookmarkType = request.getType(); // 타입이 이미 BookmarkType으로 제공됨
        Bookmark bookmark = bookmarkRepository.findByUserIdAndCommIdAndTargetIdAndType(
                userId,
                request.getCommId(),
                request.getTargetId(),
                bookmarkType
        ).orElseThrow(() -> new BookmarkNotFoundException(ErrorType.BOOKMARK_NOT_FOUND));

        if (!bookmark.getUserId().equals(userId)) {
            throw new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        bookmarkRepository.delete(bookmark);
    }



    /**
     * 특정 질문 북마크 상태 확인
     */
    public boolean isQuestionBookmarked(Long userId, Long commId, Long questionId) {
        return bookmarkRepository.existsByUserIdAndCommIdAndTargetIdAndType(userId, commId, questionId, BookmarkType.QUESTION);
    }

    /**
     * 특정 답변 북마크 상태 확인
     */
    public boolean isAnswerBookmarked(Long userId, Long commId, Long answerId) {
        return bookmarkRepository.existsByUserIdAndCommIdAndTargetIdAndType(userId, commId, answerId, BookmarkType.ANSWER);
    }
}
