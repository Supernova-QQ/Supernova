package com.hanshin.supernova.question.application;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionService {
    
    private final QuestionRepository questionRepository;

    public QuestionSaveResponse createQuestion(Long cId, QuestionRequest request) {

        if (request.getTitle().isEmpty() || request.getContent().isEmpty()) {
            throw new QuestionInvalidException(ErrorType.NEITHER_BLANK_ERROR);
        }
        
        // TODO user 정보 받아오기

        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .questionerId(1L)   // TODO user id
                .commId(1L)         // TODO community id
                .build();

        Question savedQuestion = questionRepository.save(question);

        // TODO hashtag save logic

        // TODO community save logic
//        communityService.registerQuestion()

        return new QuestionSaveResponse(
                savedQuestion.getId()
        );
    }
}
