package com.hanshin.supernova.hashtag.presentaion;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
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

    @PostMapping("/{q_id}/hashtags")
    public ResponseEntity<?> saveHashtag(
            @PathVariable(name = "q_id") Long qId,
            @RequestBody @Valid HashtagRequest hashtagRequest,
            AuthUser authUser
    ) {
        var response = hashtagService.saveQuestionHashtag(qId, hashtagRequest, authUser);
        return ResponseDto.created(response);
    }

    @GetMapping("/{q_id}/hashtags")
    public ResponseEntity<?> getAllHashtags(
            @PathVariable(name = "q_id") Long qId
    ) {
        var response = hashtagService.getHashtagNames(qId);
        return ResponseDto.ok(response);
    }
}
