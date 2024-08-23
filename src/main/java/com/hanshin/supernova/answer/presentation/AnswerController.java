package com.hanshin.supernova.answer.presentation;

import com.hanshin.supernova.answer.application.AnswerService;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.common.model.ResponseDto;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/questions/{q_id}/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<?> createAnswer(
            @PathVariable("q_id") Long qId,
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.createAnswer(qId, request);
        return ResponseDto.created(response);
    }

    @GetMapping(path = "/{a_id}")
    public ResponseEntity<?> getAnswer(
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.getAnswer(qId, aId);
        return ResponseDto.ok(response);
    }

    @PutMapping(path = "/{a_id}")
    public ResponseEntity<?> updateAnswer(
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId,
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.editAnswer(qId, aId, request);
        return ResponseDto.ok(response);

    }

    @DeleteMapping(path = "/{a_id}")
    public ResponseEntity<?> deleteAnswer(
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.deleteAnswer(qId, aId);
        return ResponseDto.ok(response);

    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getAnswerList(
            @PathVariable("q_id") Long qId
    ) {
        List<AnswerResponse> responses = answerService.getAnswerList(qId);
        return ResponseDto.ok(responses);
    }

    @PatchMapping(path = "/{a_id}")
    public ResponseEntity<?> acceptAnswer(
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.acceptAnswer(qId, aId);
        return ResponseDto.ok(response);

    }
}
