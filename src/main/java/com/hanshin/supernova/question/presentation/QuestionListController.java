package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.question.application.QuestionListService;
import com.hanshin.supernova.question.dto.response.QuestionInfoResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/communities/{c_id}")
@RequiredArgsConstructor
public class QuestionListController {

    private final QuestionListService questionListService;

    @GetMapping(path = "/all-latest-questions")
    public ResponseEntity<?> allLatestQuestions(
            @PathVariable("c_id") Long cId
    ) {
        var response = questionListService.getAllQuestionsByDesc(cId);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/all-old-questions")
    public ResponseEntity<?> allOldQuestions(
            @PathVariable("c_id") Long cId
    ) {
        var response = questionListService.getAllQuestionsByAsc(cId);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/unanswered-latest-questions")
    public ResponseEntity<?> unansweredLatestQuestions(
            @PathVariable("c_id") Long cId
    ) {
        List<QuestionInfoResponse> responses = questionListService.getUnAnsweredQuestionsByDesc(
                cId);
        return ResponseDto.ok(responses);
    }

    @GetMapping(path = "/unanswered-old-questions")
    public ResponseEntity<?> unansweredOldQuestions(
            @PathVariable("c_id") Long cId
    ) {
        List<QuestionInfoResponse> responses = questionListService.getUnAnsweredQuestionsByAsc(
                cId);
        return ResponseDto.ok(responses);
    }

    @GetMapping(path = "/unanswered-latest-4-questions")
    public ResponseEntity<?> unansweredLatest4Questions(
            @PathVariable("c_id") Long cId
    ) {
        List<QuestionInfoResponse> responses = questionListService.getUnAnswered4QuestionsByDesc(
                cId, 4);
        return ResponseDto.ok(responses);
    }
}
