package com.hanshin.supernova.answer.presentation;

import com.hanshin.supernova.answer.application.AiCommentService;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.orchestration.application.QuestionOrchestrator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/questions/{q_id}/ai-answers")
@RequiredArgsConstructor
public class AiCommentController {

    private final AiCommentService aiCommentService;
    private final QuestionOrchestrator questionOrchestrator;

    @GetMapping
    public ResponseEntity<?> getAiComment(
            @PathVariable("q_id") Long questionId
    ) {
        var responses = aiCommentService.getAiComment(questionId);
        return ResponseDto.ok(responses);
    }

    @PutMapping
    public ResponseEntity<?> updateAiComment(
            AuthUser user,
            @PathVariable("q_id") Long questionId
    ) {
        var responses = questionOrchestrator.updateAiAnswer(user, questionId);
        return ResponseDto.ok(responses);
    }
}
