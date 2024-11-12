package com.hanshin.supernova.answer.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.answer.application.AnswerService;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
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

    @PostMapping
    public ResponseEntity<?> createAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.createAnswer(user, qId, request);
        return ResponseDto.created(response);
    }

    @GetMapping(path = "/{a_id}")
    public ResponseEntity<?> getAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.getAnswer(user, qId, aId);
        return ResponseDto.ok(response);
    }

    @PutMapping(path = "/{a_id}")
    public ResponseEntity<?> updateAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId,
            @RequestBody @Valid AnswerRequest request
    ) {
        var response = answerService.editAnswer(user, qId, aId, request);
        return ResponseDto.ok(response);

    }

    @DeleteMapping(path = "/{a_id}")
    public ResponseEntity<?> deleteAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.deleteAnswer(user, qId, aId);
        return ResponseDto.ok(response);

    }

    @GetMapping
    public ResponseEntity<?> getAnswerList(
            @PathVariable("q_id") Long qId
    ) {
        List<AnswerResponse> responses = answerService.getAnswerList(qId);
        return ResponseDto.ok(responses);
    }

    @PatchMapping(path = "/{a_id}")
    public ResponseEntity<?> acceptAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.acceptAnswer(user, qId, aId);
        return ResponseDto.ok(response);

    }

    /**
     * 기존 추천 이력 유무에 따라 추천수 증감
     */
    @PostMapping(path = "/{a_id}/recommendation")
    public ResponseEntity<?> recommendAnswer(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @PathVariable("a_id") Long aId
    ) {
        var response = answerService.updateAnswerRecommendation(user, aId);
        return ResponseDto.ok(response);
    }
}
