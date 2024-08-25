package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(path = "/questions")
    public ResponseEntity<?> createQuestion(
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.createQuestion(request);
        return ResponseDto.created(response);
    }

    @GetMapping(path = "/questions/{q_id}")
    public ResponseEntity<?> readQuestion(
            @PathVariable("q_id") Long q_id) {
        var response = questionService.getQuestion(q_id);
        return ResponseDto.ok(response);
    }

    @PutMapping(path = "/questions/{q_id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable("q_id") Long q_id,
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.editQuestion(q_id, request);
        return ResponseDto.ok(response);
    }

    @DeleteMapping(path = "/questions/{q_id}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable("q_id") Long q_id
    ) {
        var response = questionService.deleteQuestion(q_id);
        return ResponseDto.ok(response);
    }

    // TODO 커뮤니티 목록 제공 api 추가

//    @GetMapping(path = "/communities/{c_id}/unanswered-questions")
//    public ResponseEntity<?> unansweredQuestionList(
//            @PathVariable("c_id") Long c_id) {
//        var response = questionService.getUnAnsweredQuestions(c_id);
//        return ResponseDto.ok(response);
//    }
//
//    @GetMapping(path = "/communities/{c_id}/questions")
//    public ResponseEntity<?> allQuestionList(
//            @PathVariable("c_id") Long c_id
//    ) {
//        var response = questionService.getAllQuestions(c_id);
//        return ResponseDto.ok(response);
//    }

}
