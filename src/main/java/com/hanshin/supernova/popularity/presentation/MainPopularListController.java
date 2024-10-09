package com.hanshin.supernova.popularity.presentation;

import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.popularity.application.MainPopularListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/main")
@RequiredArgsConstructor
public class MainPopularListController {

    private final MainPopularListService mainPopularListService;

    /**
     * 근 30일 최다 방문객수 커뮤니티 3개
     */
    @GetMapping("/premonth-top3-communities")
    public ResponseEntity<?> preMonthTop3Communities() {
        var responses = mainPopularListService.getTop3Communities();
        return ResponseDto.ok(responses);
    }

    /**
     * 근 30일 최다 조회수 질문 5개
     */
    @GetMapping("/premonth-top5-questions")
    public ResponseEntity<?> preMonthTop5Questions() {
        var responses = mainPopularListService.getTop5Questions();
        return ResponseDto.ok(responses);
    }

    /**
     * 전날 최다 사용된 해시태그 10개
     */
    @GetMapping("/preday-top10-hashtag")
    public ResponseEntity<?> preDayTop10Hashtag() {
         var responses = mainPopularListService.getTop10Hashtags();
         return ResponseDto.ok(responses);
    }

    /**
     * 전날 최다 조회수 질문 1개
     */
    @GetMapping("/preday-most-viewed-question")
    public ResponseEntity<?> preDayMostViewedQuestion() {
        var responses = mainPopularListService.getMostViewedQuestion();
        return ResponseDto.ok(responses);
    }

    /**
     * 전날 최다 추천수 답변 1개
     */
//    @GetMapping("/preday-most-viewed-answer")
//    public ResponseEntity<?> preDayMostViewedAnswer() {
//        var response = mainPopularListService.getMostLikedAnswer();
//        return ResponseDto.ok(response);
//    }

}
