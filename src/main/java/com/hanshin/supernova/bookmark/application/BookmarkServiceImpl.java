package com.hanshin.supernova.bookmark.application;

import com.hanshin.supernova.bookmark.domain.Bookmark;
import com.hanshin.supernova.bookmark.infrastructure.BookmarkRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    /**
     * 질문에 대한 북마크 추가
     */
    @Override
    @Transactional
    public void addQuestionBookmark(HttpServletRequest request, Long questionId) {
        User user = getUserFromClaims(request);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR));
        bookmarkRepository.findByUserAndQuestion(user, question)
                .orElseGet(() -> bookmarkRepository.save(Bookmark.builder().user(user).question(question).build()));
    }

    /**
     * 질문에 대한 북마크 삭제
     */
    @Override
    @Transactional
    public void removeQuestionBookmark(HttpServletRequest request, Long questionId) {
        User user = getUserFromClaims(request);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR));
        bookmarkRepository.findByUserAndQuestion(user, question)
                .ifPresent(bookmarkRepository::delete);
    }

    /**
     * 답변에 대한 북마크 추가
     */
    @Override
    @Transactional
    public void addAnswerBookmark(HttpServletRequest request, Long answerId) {
        User user = getUserFromClaims(request);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new QuestionInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR));
        bookmarkRepository.findByUserAndAnswer(user, answer)
                .orElseGet(() -> bookmarkRepository.save(Bookmark.builder().user(user).answer(answer).build()));
    }

    /**
     * 답변에 대한 북마크 삭제
     */
    @Override
    @Transactional
    public void removeAnswerBookmark(HttpServletRequest request, Long answerId) {
        User user = getUserFromClaims(request);
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new QuestionInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR));
        bookmarkRepository.findByUserAndAnswer(user, answer)
                .ifPresent(bookmarkRepository::delete);
    }

    /**
     * 북마크된 질문 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> getBookmarkedQuestions(HttpServletRequest request) {
        User user = getUserFromClaims(request);
        return bookmarkRepository.findAllByUserAndQuestionIsNotNull(user);
    }

    /**
     * 북마크된 답변 목록 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<Bookmark> getBookmarkedAnswers(HttpServletRequest request) {
        User user = getUserFromClaims(request);
        return bookmarkRepository.findAllByUserAndAnswerIsNotNull(user);
    }

    private User getUserFromClaims(HttpServletRequest request) {
        // HttpServletRequest에서 Claims 객체를 가져와 User 정보를 반환
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        String email = claims.getSubject();
        if (email == null) {
            throw new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR);
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR));
    }
}
