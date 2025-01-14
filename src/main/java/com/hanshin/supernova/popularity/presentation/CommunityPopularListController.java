package com.hanshin.supernova.popularity.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.popularity.application.CommunityPopularListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/communities/{c_id}")
@RequiredArgsConstructor
public class CommunityPopularListController {

    private final CommunityPopularListService communityPopularListService;

    @Operation(summary = "커뮤니티 내부 인기 게시글 4개 목록 조회", description = "커뮤니티 메인 화면에서 게시글 4개의 목록을 조회한다.")
    @GetMapping("/popular-4-questions")
    public ResponseEntity<?> popular4Questions(
            @Parameter(description = "커뮤니티 고유 번호") @PathVariable("c_id") Long c_id) {
        var responses = communityPopularListService.getPopular4QuestionsByCommunity(c_id);
        return ResponseDto.ok(responses);
    }

    @Operation(summary = "커뮤니티 내부 인기 게시글 전체 목록 조회", description = "커뮤니티 인기 게시글 더보기에서 전체 인기 목록을 조회한다.")
    @GetMapping("/popular-questions")
    public ResponseEntity<?> popularQuestions(
            @Parameter(description = "커뮤니티 고유 번호") @PathVariable("c_id") Long c_id) {
        var responses = communityPopularListService.getPopularQuestionsByCommunity(c_id);
        return ResponseDto.ok(responses);
    }
}
