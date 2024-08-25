package com.hanshin.supernova.answer.dto.response;

import com.hanshin.supernova.answer.domain.Tag;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AnswerResponse {

    private Long id;
    private String nickname;
    private String answer;
    private LocalDateTime createdAt;
    private int recCnt;
    private Tag tag;
    private String source;
    private boolean isAi;
    private boolean isAccepted;

    public static AnswerResponse toResponse(Long id, String nickname, String answer,
            LocalDateTime createdAt, int recCnt,
            Tag tag, String source, boolean isAi, boolean isAccepted) {
        return new AnswerResponse(id, nickname, answer, createdAt, recCnt, tag, source, isAi,
                isAccepted);
    }
}
