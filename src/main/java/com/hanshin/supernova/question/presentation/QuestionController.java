package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.orchestration.application.QuestionOrchestrator;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.CommunityInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @Operation(summary = "게시글 생성")
    @PostMapping
    public ResponseEntity<?> createQuestion(
            AuthUser user,
            @Parameter(required = true, description = "게시글 생성 요청")
            @RequestBody @Valid QuestionRequest request) {
        var response = questionOrchestrator.createQuestionWithAiAnswer(user, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "게시글 조회")
    @GetMapping(path = "/{q_id}")
    public ResponseEntity<?> readQuestion(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long q_id) {
        var response = questionService.getQuestion(q_id);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 수정")
    @PutMapping(path = "/{q_id}")
    public ResponseEntity<?> updateQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long q_id,
            @Parameter(required = true, description = "게시글 수정 요청")
            @RequestBody @Valid QuestionRequest request) {
        var response = questionOrchestrator.updateQuestionWithHashtag(user, q_id, request);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping(path = "/{q_id}")
    public ResponseEntity<?> deleteQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long q_id
    ) {
        var response = questionService.deleteQuestion(user, q_id);
        return ResponseDto.ok(response);
    }

    /**
     * 기존 추천 이력 유무에 따라 추천수 증감
     */
    @Operation(summary = "게시글 추천")
    @PostMapping(path = "/{q_id}/recommendation")
    public ResponseEntity<?> recommendQuestion(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId
    ) {
        var response = questionService.updateQuestionRecommendation(user, qId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "내 커뮤니티 목록 조회", description = "게시글 생성 시 내가 속한 커뮤니티의 목록을 선택하는 데 사용됩니다.")
    @GetMapping(path = "/my-communities")
    public ResponseEntity<?> getMyCommunities(
            AuthUser user
    ) {
        List<CommunityInfoResponse> responses = questionService.getMyCommunities(user);
        return ResponseDto.ok(responses);
    }

    // questionId로 communityId 조회
    @Operation(summary = "게시글이 속한 커뮤니티 조회")
    @GetMapping("/{q_id}/c_id")
    public ResponseEntity<Long> getCommunityIdByQuestionId(
            @Parameter(description = "게시글 고유 번호") @PathVariable(name = "q_id") Long q_id) {
        Long communityId = questionService.findCommunityIdByQuestionId(q_id);
        return ResponseEntity.ok(communityId);
    }
}
