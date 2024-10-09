package com.hanshin.supernova.popularity.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.popularity.application.CommunityPopularListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/communities/{c_id}")
@RequiredArgsConstructor
public class CommunityPopularListController {

    private final CommunityPopularListService communityPopularListService;

    @GetMapping("/popular-4-questions")
    public ResponseEntity<?> popular4Questions(@PathVariable("c_id") Long c_id) {
        var responses = communityPopularListService.getPopular4QuestionsByCommunity(c_id);
        return ResponseDto.ok(responses);
    }

    @GetMapping("/popular-questions")
    public ResponseEntity<?> popularQuestions(@PathVariable("c_id") Long c_id) {
        var responses = communityPopularListService.getPopularQuestionsByCommunity(c_id);
        return ResponseDto.ok(responses);
    }
}
