package com.hanshin.supernova.answer.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.answer.application.AnswerService;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/questions/{q_id}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @Operation(summary = "답변 생성")
    @PostMapping
    public ResponseEntity<?> createAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(required = true, description = "답변 생성 요청")
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.createAnswer(user, qId, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "답변 조회")
    @GetMapping(path = "/{a_id}")
    public ResponseEntity<?> getAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(description = "답변 고유 번호")
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.getAnswer(user, qId, aId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "답변 수정")
    @PutMapping(path = "/{a_id}")
    public ResponseEntity<?> updateAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(description = "답변 고유 번호")
            @PathVariable("a_id") Long aId,
            @Parameter(required = true, description = "답변 수정 요청")
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.editAnswer(user, qId, aId, request);
        return ResponseDto.ok(response);

    }

    @Operation(summary = "답변 삭제")
    @DeleteMapping(path = "/{a_id}")
    public ResponseEntity<?> deleteAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(description = "답변 고유 번호")
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.deleteAnswer(user, qId, aId);
        return ResponseDto.ok(response);

    }

    @Operation(summary = "답변 목록 조회")
    @GetMapping
    public ResponseEntity<?> getAnswerList(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId
    ) {
        List<AnswerResponse> responses = answerService.getAnswerList(qId);
        return ResponseDto.ok(responses);
    }

    @Operation(summary = "답변 채택")
    @PatchMapping(path = "/{a_id}")
    public ResponseEntity<?> acceptAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(description = "답변 고유 번호")
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.acceptAnswer(user, qId, aId);
        return ResponseDto.ok(response);

    }

    /**
     * 기존 추천 이력 유무에 따라 추천수 증감
     */
    @Operation(summary = "답변 추천")
    @PostMapping(path = "/{a_id}/recommendation")
    public ResponseEntity<?> recommendAnswer(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long qId,
            @Parameter(description = "답변 고유 번호")
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.updateAnswerRecommendation(user, aId);
        return ResponseDto.ok(response);
    }
}
