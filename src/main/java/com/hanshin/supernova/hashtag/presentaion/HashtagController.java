package com.hanshin.supernova.hashtag.presentaion;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.hashtag.application.HashtagService;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/hashtags")
@RequiredArgsConstructor
public class HashtagController {

    private final HashtagService hashtagService;

    @PostMapping("/questions/{q_id}")
    public ResponseEntity<?> saveHashtag(
            @PathVariable(name = "q_id") Long qId,
            @RequestBody @Valid HashtagRequest hashtagRequest
    ) {
        var response = hashtagService.saveQuestionHashtag(qId, hashtagRequest);
        return ResponseDto.created(response);
    }

    @GetMapping("/questions/{q_id}")
    public ResponseEntity<?> getAllHashtags(
            @PathVariable(name = "q_id") Long qId
    ) {
        var response = hashtagService.getHashtagNames(qId);
        return ResponseDto.ok(response);
    }
}
