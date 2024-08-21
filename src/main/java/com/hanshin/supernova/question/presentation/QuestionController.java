package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
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

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createQuestion(
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.createQuestion(request);
        return ResponseDto.created(response);
    }

    @GetMapping(path = "/{q_id}")
    public ResponseEntity<?> readQuestion(
            @PathVariable("q_id") Long q_id) {
        var response = questionService.getQuestion(q_id);
        return ResponseDto.ok(response);
    }

    @PutMapping(path = "/{q_id}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable("q_id") Long q_id,
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.editQuestion(q_id, request);
        return ResponseDto.ok(response);
    }

    @DeleteMapping(path = "/{q_id}")
    public ResponseEntity<?> deleteQuestion(
            @PathVariable("q_id") Long q_id
    ) {
        var response = questionService.deleteQuestion(q_id);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{q_id}/my-communities")
    public ResponseEntity<?> getMyCommunities(
            @PathVariable("q_id") Long q_id
    ) {
        List<CommunityInfoResponse> responses = questionService.getMyCommunities(q_id);
        return ResponseDto.ok(responses);
    }

}
