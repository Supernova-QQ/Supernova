package com.hanshin.supernova.common.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.answer.AnswerInvalidException;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractValidateService {

    protected UserRepository userRepository;
    protected CommunityRepository communityRepository;
    protected QuestionRepository questionRepository;
    protected AnswerRepository answerRepository;

    @Autowired
    public void setRepository(UserRepository userRepository, CommunityRepository communityRepository, QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    protected User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

    protected Community getCommunityOrThrowIfNotExist(Long cId) {
        return communityRepository.findById(cId).orElseThrow(
                () -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR)
        );
    }

    protected Question getQuestionOrThrowIfNotExist(Long qId) {
        return questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

    protected Answer getAnswerOrThrowIfNotExist(Long aId) {
        return answerRepository.findById(aId).orElseThrow(
                () -> new AnswerInvalidException(ErrorType.ANSWER_NOT_FOUND_ERROR)
        );
    }

    protected static void verifyAdmin(User user) {
        if (!user.getAuthority().equals(Authority.ADMIN)) {
            throw new AuthInvalidException(ErrorType.ONLY_ADMIN_AUTHORITY_ERROR);
        }
    }

    protected static void verifySameUser(Long comparedUserId, Long originalUserId) {
        if (!comparedUserId.equals(originalUserId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }
}
