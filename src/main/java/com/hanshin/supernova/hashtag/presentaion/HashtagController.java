package com.hanshin.supernova.hashtag.presentaion;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/questions")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @Operation(summary = "게시글에 대한 해시태그 등록")
    @PostMapping("/{q_id}/hashtags")
    public ResponseEntity<?> saveHashtag(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable(name = "q_id") Long qId,
            @Parameter(required = true, description = "해시태그 등록 요청")
            @RequestBody @Valid HashtagRequest hashtagRequest,
            AuthUser authUser
    ) {
        var response = hashtagService.saveQuestionHashtag(qId, hashtagRequest, authUser);
        return ResponseDto.created(response);
    }

    @Operation(summary = "게시글에 대한 해시태그 목록 조회")
    @GetMapping("/{q_id}/hashtags")
    public ResponseEntity<?> getAllHashtags(
            @Parameter(description = "게시글 고유 번호")
            @PathVariable(name = "q_id") Long qId
    ) {
        var response = hashtagService.getHashtagNames(qId);
        return ResponseDto.ok(response);
    }
}
