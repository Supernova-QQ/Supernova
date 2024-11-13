package com.hanshin.supernova.bookmark.application;

import com.hanshin.supernova.bookmark.domain.Bookmark;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface BookmarkService {

    /**
     * 질문에 대한 북마크 추가
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @param questionId 북마크할 질문의 ID
     */
    void addQuestionBookmark(HttpServletRequest request, Long questionId);

    /**
     * 질문에 대한 북마크 삭제
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @param questionId 삭제할 질문의 ID
     */
    void removeQuestionBookmark(HttpServletRequest request, Long questionId);

    /**
     * 답변에 대한 북마크 추가
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @param answerId 북마크할 답변의 ID
     */
    void addAnswerBookmark(HttpServletRequest request, Long answerId);

    /**
     * 답변에 대한 북마크 삭제
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @param answerId 삭제할 답변의 ID
     */
    void removeAnswerBookmark(HttpServletRequest request, Long answerId);

    /**
     * 북마크된 질문 목록 조회
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @return 사용자 ID에 해당하는 북마크된 질문 목록
     */
    List<Bookmark> getBookmarkedQuestions(HttpServletRequest request);

    /**
     * 북마크된 답변 목록 조회
     * @param request 사용자 인증 정보가 포함된 HttpServletRequest 객체
     * @return 사용자 ID에 해당하는 북마크된 답변 목록
     */
    List<Bookmark> getBookmarkedAnswers(HttpServletRequest request);
}
