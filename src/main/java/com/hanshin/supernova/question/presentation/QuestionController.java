package com.hanshin.supernova.question.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.question.application.QuestionService;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/communities/{c_id}/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createQuestion(@PathVariable("c_id") Long c_id,
            @RequestBody @Valid QuestionRequest request) {
        var response = questionService.createQuestion(c_id, request);
        return ResponseDto.created(response);
    }

}
