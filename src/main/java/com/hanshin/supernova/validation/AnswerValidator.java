package com.hanshin.supernova.validation;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerValidator {
    private final AnswerRepository answerRepository;

    public Answer getAnswerOrThrowIfNotExist(Long aId) {
        return answerRepository.findById(aId).orElseThrow(
                () -> new AnswerInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR)
        );
    }
}
