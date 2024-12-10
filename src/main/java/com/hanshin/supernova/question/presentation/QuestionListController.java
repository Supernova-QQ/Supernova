package com.hanshin.supernova.question.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.question.application.QuestionListService;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/communities")
@RequiredArgsConstructor
public class QuestionListController {

    private final QuestionListService questionListService;

    /**
     * 일반 게시판을 제외하고 전체 커뮤니티 최신 질문 N개 목록
     */
    @GetMapping(path = "/N-latest-questions")
    public ResponseEntity<?> nLatestQuestions() {
        List<QuestionInfoResponse> responses = questionListService.getNLatestQuestionsByDesc(5);
        return ResponseDto.ok(responses);
    }

    /**
     * 일반 게시판을 제외하고 전체 커뮤니티 최신 질문 전체 목록
     */
//    @GetMapping(path = "/all-latest-questions")
//    public ResponseEntity<?> allLatestQuestions(
//            @PageableDefault(size = 7) Pageable pageable) {
//        Page<QuestionInfoResponse> responses = questionListService.getAllLatestQuestionsByDesc(pageable);
//        return ResponseDto.ok(responses);
//    }

    /**
     * 일반 게시판 최신 질문 4개 목록
     */
    @GetMapping(path = "/N-latest-questions-from-general")
    public ResponseEntity<?> nLatestQuestionsFromGeneralCommunity() {
        List<QuestionInfoResponse> responses = questionListService.getNLatestQuestionsFromGeneralCommunityByDesc(5);
        return ResponseDto.ok(responses);
    }

    /**
     * 일반 게시판 최신 질문 전체 목록
     */
//    @GetMapping(path = "/all-latest-questions-from-general")
//    public ResponseEntity<?> allLatestQuestionsFromGeneralCommunity(
//            @PageableDefault(size = 7) Pageable pageable) {
//        Page<QuestionInfoResponse> responses = questionListService.getAllLatestQuestionsFromGeneralCommunityByDesc(pageable);
//        return ResponseDto.ok(responses);
//    }


    @GetMapping(path = "/{c_id}/all-latest-questions")
    public ResponseEntity<?> allLatestQuestionsByCommunity(
            @PathVariable("c_id") Long cId,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<QuestionInfoResponse> response = questionListService.getAllQuestionsByDesc(
                cId, pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{c_id}/all-old-questions")
    public ResponseEntity<?> allOldQuestions(
            @PathVariable("c_id") Long cId,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<QuestionInfoResponse> response = questionListService.getAllQuestionsByAsc(cId,
                pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{c_id}/unanswered-latest-questions")
    public ResponseEntity<?> unansweredLatestQuestions(
            @PathVariable("c_id") Long cId,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<QuestionInfoResponse> responses = questionListService.getUnAnsweredQuestionsByDesc(
                cId, pageable);
        return ResponseDto.ok(responses);
    }

    @GetMapping(path = "/{c_id}/unanswered-old-questions")
    public ResponseEntity<?> unansweredOldQuestions(
            @PathVariable("c_id") Long cId,
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<QuestionInfoResponse> responses = questionListService.getUnAnsweredQuestionsByAsc(
                cId, pageable);
        return ResponseDto.ok(responses);
    }

    /**
     * 답변을 기다리는 최신 4개 질문
     */
    @GetMapping(path = "/{c_id}/unanswered-latest-4-questions")
    public ResponseEntity<?> unansweredLatest4Questions(
            @PathVariable("c_id") Long cId
    ) {
        List<QuestionInfoResponse> responses = questionListService.getUnAnswered4QuestionsByDesc(
                cId, 4);
        return ResponseDto.ok(responses);
    }
}
