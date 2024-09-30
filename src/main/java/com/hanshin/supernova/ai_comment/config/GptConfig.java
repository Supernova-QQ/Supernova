package com.hanshin.supernova.ai_comment.config;

import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/*
 * CHAT GPT 라이브러리를 사용하기 전, 해당 서비스에 토큰을 주입하기 위한 Config
 */
@Slf4j
@Configuration
public class GptConfig {

    public final static String MODEL = "gpt-3.5-turbo-0125";
    public final static Duration TIME_OUT = Duration.ofSeconds(300);

    @Value("${openai.api.key}")
    private String token;

    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(token, TIME_OUT);
    }

    public ResponseEntity<String> requestGPT(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + token);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        List<Map<String, String >> messages = new ArrayList<>();
        Map<String , String > map = new HashMap<>();
        map.put("role", "user");

        map.put("content", prompt);
        messages.add(map);
        requestBody.put("messages", messages);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForEntity("https://api.openai.com/v1/chat/completions", entity, String.class);
    }
}
