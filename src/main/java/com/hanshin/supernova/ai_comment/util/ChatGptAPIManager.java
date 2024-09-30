package com.hanshin.supernova.ai_comment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanshin.supernova.ai_comment.dto.response.AiAnswerResponse;
import com.hanshin.supernova.ai_comment.config.GptConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatGptAPIManager {

    private final GptConfig gptConfig;
    private final JSONParser parser = new JSONParser();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // AI 답변 생성
    public AiAnswerResponse generateAiAnswer(String title, String content) throws ParseException, JsonProcessingException {
        ResponseEntity<String> aiResponse = gptConfig.requestGPT(Prompt.generateAiRequest(title, content));

        // 응답 처리
        String response = getMessageContent(aiResponse);

        JSONObject contentObject = (JSONObject) parser.parse(response);
        String answer = (String) contentObject.get("답변");
        return AiAnswerResponse.toResponse(answer);
    }

    // AI 답변 재생성
    public AiAnswerResponse regenerateAiAnswer(String title, String content, String preAnswer) throws ParseException, JsonProcessingException {
        ResponseEntity<String> aiResponse = gptConfig.requestGPT(Prompt.regenerateAiRequest(title, content, preAnswer));

        // 응답 처리
        String response = getMessageContent(aiResponse);

        JSONObject contentObject = (JSONObject) parser.parse(response);
        String answer = (String) contentObject.get("답변");
        return AiAnswerResponse.toResponse(answer);
    }

    private String getMessageContent(ResponseEntity<String> gptResponse) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(gptResponse.getBody());
        return rootNode
                .path("choices")    // "choices" 배열을 가져온다.
                .path(0)            // 첫 번째 선택을 가져온다.
                .path("message")    // "message" 객체를 가져온다.
                .path("content")    // "content" 필드의 값을 가져오는데, 이는 실제 생성된 메시지 내용이다.
                .asText();
    }
}