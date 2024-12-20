package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.orchestration.application.QuestionOrchestrator;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
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
        var response = questionOrchestrator.updateQuestionWithHashtag(user, q_id, request);
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

    @GetMapping(path = "/my-communities")
    public ResponseEntity<?> getMyCommunities(
            AuthUser user
    ) {
        List<CommunityInfoResponse> responses = questionService.getMyCommunities(user);
        return ResponseDto.ok(responses);
    }

    // questionId로 communityId 조회
    @GetMapping("/{q_id}/c_id")
    public ResponseEntity<Long> getCommunityIdByQuestionId(@PathVariable(name = "q_id") Long q_id) {
        Long communityId = questionService.findCommunityIdByQuestionId(q_id);
        return ResponseEntity.ok(communityId);
    }
}
