package com.hanshin.supernova.my.dto.response;

import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerWithQuestionResponse {
    private AnswerResponse answerResponse;
    private String questionTitle; // 질문 제목
    private String communityImg;  // 커뮤니티 이미지 URL

}
