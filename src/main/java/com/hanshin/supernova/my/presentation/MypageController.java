package com.hanshin.supernova.my.presentation;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.my.application.MypageService;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.my.dto.response.AnswerWithQuestionResponse;
import com.hanshin.supernova.my.dto.response.MyCommunityResponse;
import com.hanshin.supernova.my.dto.response.MyQuestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/my")
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    /**
     * 질문 제목과 함께 답변 생성
     */
    @PostMapping("/questions/{q_id}/answers/with-question-title")
    public AnswerWithQuestionResponse createAnswerWithQuestionTitle(
            AuthUser user,
            @PathVariable("q_id") Long qId,
            @RequestBody @Valid AnswerRequest request
    ) {
        return mypageService.createAnswerWithQId(user, qId, request);
    }

    /**
     * 마이페이지 답변 조회
     */
    @GetMapping("/answers")
    public List<AnswerWithQuestionResponse> getMyAnswers(AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }
        return mypageService.getAnswersByUserWithQuestionTitle(authUser);
    }

    /**
     * 마이페이지 질문 조회
     */
    @GetMapping("/questions")
    public List<MyQuestionResponse> getMyQuestions(AuthUser authUser) {
        log.info("getMyQuestions AuthUser : {}", authUser);

        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }
        List<MyQuestionResponse> responses = mypageService.getQuestionsByUser(authUser);
        log.info("Returning Responses: {}", responses);

        return responses;
    }

    @GetMapping("/communities")
    public ResponseEntity<List<MyCommunityResponse>> getMyCommunities(AuthUser authUser) {
        if (authUser == null) {
            throw new AuthInvalidException(ErrorType.USER_NOT_FOUND_ERROR);
        }

        List<MyCommunityResponse> communities = mypageService.getCommunitiesByUser(authUser.getId());
        return ResponseEntity.ok(communities);
    }
}
