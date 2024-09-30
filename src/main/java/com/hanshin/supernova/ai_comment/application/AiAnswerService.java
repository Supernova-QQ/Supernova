package com.hanshin.supernova.ai_comment.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hanshin.supernova.ai_comment.dto.response.AiAnswerResponse;
import com.hanshin.supernova.ai_comment.util.ChatGptAPIManager;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.gpt.GptInvalidException;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class AiAnswerService {

    private final ChatGptAPIManager chatGptAPIManager;

    public AiAnswerResponse generateAiAnswer(String title, String content) {
        try {
            return chatGptAPIManager.generateAiAnswer(title, content);
        } catch (ParseException | JsonProcessingException e) {
            throw new GptInvalidException(ErrorType.JSON_PARSE_EXCEPTION);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                throw new GptInvalidException(ErrorType.GPT_UNAUTHORIZED);
            } else if (e.getStatusCode().value() == 429) {
                throw new GptInvalidException(ErrorType.GPT_RATE_LIMIT_EXCEEDED);
            } else {
                throw new GptInvalidException(ErrorType.GPT_REQUEST_FAILED);
            }
        }
    }

    public AiAnswerResponse regenerateAiAnswer(String title, String content, String preAnswer) {
        try {
            return chatGptAPIManager.regenerateAiAnswer(title, content, preAnswer);
        } catch (ParseException | JsonProcessingException e) {
            throw new GptInvalidException(ErrorType.JSON_PARSE_EXCEPTION);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 401) {
                throw new GptInvalidException(ErrorType.GPT_UNAUTHORIZED);
            } else if (e.getStatusCode().value() == 429) {
                throw new GptInvalidException(ErrorType.GPT_RATE_LIMIT_EXCEEDED);
            } else {
                throw new GptInvalidException(ErrorType.GPT_REQUEST_FAILED);
            }
        }
    }
}