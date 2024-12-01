package com.hanshin.supernova.my.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyQuestionResponse {
    private Long questionId;   // 질문 ID
    private Long communityId; // 커뮤니티 ID
    private String title;  // 질문 제목
    private String imgUrl;   // 커뮤니티 이미지 URL
}
