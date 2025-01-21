package com.hanshin.supernova.validation;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionValidator {
    private final QuestionRepository questionRepository;

    public Question getQuestionOrThrowIfNotExist(Long qId) {
        return questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }
}
