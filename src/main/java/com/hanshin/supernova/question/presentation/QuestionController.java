package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.orchestration.application.QuestionOrchestrator;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping(path = "/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionOrchestrator questionOrchestrator;
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createQuestion(
            AuthUser user,
            @RequestBody @Valid QuestionRequest request) {
        var response = questionOrchestrator.createQuestionWithAiAnswer(user, request);
        return ResponseDto.created(response);
    }

    @GetMapping(path = "/{q_id}")
    public ResponseEntity<?> readQuestion(
            AuthUser user,
            @PathVariable("q_id") Long q_id) {
        var response = questionService.getQuestion(user, q_id);
        return ResponseDto.ok(response);
    }

    @PutMapping(path = "/{q_id}")
    public ResponseEntity<?> updateQuestion(
            AuthUser user,
            @PathVariable("q_id") Long q_id,
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.editQuestion(user, q_id, request);
        return ResponseDto.ok(response);
    }

    @DeleteMapping(path = "/{q_id}")
    public ResponseEntity<?> deleteQuestion(
            AuthUser user,
            @PathVariable("q_id") Long q_id
    ) {
        var response = questionService.deleteQuestion(user, q_id);
        return ResponseDto.ok(response);
    }

    /**
     * 기존 추천 이력 유무에 따라 추천수 증감
     */
    @PostMapping(path = "/{q_id}/recommendation")
    public ResponseEntity<?> recommendQuestion(
            AuthUser user,
            @PathVariable("q_id") Long qId
    ) {
        var response = questionService.updateQuestionRecommendation(user, qId);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{q_id}/my-communities")
    public ResponseEntity<?> getMyCommunities(
            AuthUser user,
            @PathVariable("q_id") Long q_id
    ) {
        List<CommunityInfoResponse> responses = questionService.getMyCommunities(user, q_id);
        return ResponseDto.ok(responses);
    }
}
