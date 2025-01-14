package com.hanshin.supernova.answer.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.answer.application.AiCommentService;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.orchestration.application.QuestionOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping("/api/questions/{q_id}/ai-answers")
@RequiredArgsConstructor
public class AiCommentController {

    private final AiCommentService aiCommentService;
    private final QuestionOrchestrator questionOrchestrator;

    @Operation(summary = "AI 답변 조회")
    @GetMapping
    public ResponseEntity<?> getAiComment(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long questionId
    ) {
        var responses = aiCommentService.getAiComment(questionId);
        return ResponseDto.ok(responses);
    }

    @PutMapping
    @Operation(summary = "AI 답변 재신청")
    public ResponseEntity<?> updateAiComment(
            AuthUser user,
            @Parameter(description = "게시글 고유 번호")
            @PathVariable("q_id") Long questionId
    ) {
        var responses = questionOrchestrator.updateAiAnswer(user, questionId);
        return ResponseDto.ok(responses);
    }
}
