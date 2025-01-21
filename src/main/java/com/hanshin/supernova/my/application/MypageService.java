package com.hanshin.supernova.my.application;

import com.hanshin.supernova.answer.domain.Answer;
import com.hanshin.supernova.answer.dto.request.AnswerRequest;
import com.hanshin.supernova.answer.dto.response.AnswerResponse;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.my.dto.response.AnswerWithQuestionResponse;
import com.hanshin.supernova.answer.infrastructure.AnswerRepository;
import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.my.dto.response.MyCommunityResponse;
import com.hanshin.supernova.my.dto.response.MyQuestionResponse;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.validation.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MypageService {
    private final UserValidator userValidator;

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    @Transactional
    public AnswerWithQuestionResponse createAnswerWithQId(AuthUser user, Long qId, AnswerRequest request) {
        // 질문 조회
        Question findQuestion = getQuestionById(qId);

        // 유저 조회
        User findUser = userValidator.getUserOrThrowIfNotExist(user.getId());

        // 답변 생성 및 저장
        Answer answer = buildAnswer(qId, request, findUser.getId());
        Answer savedAnswer = answerRepository.save(answer);

        // 질문 답변 카운트 증가
        findQuestion.increaseAnswerCnt();

        // 커뮤니티 이미지 조회
        String communityImg = getCommunityImageById(findQuestion.getCommId());

        // 기존 AnswerResponse 생성
        AnswerResponse answerResponse = AnswerResponse.toResponse(
                savedAnswer.getId(),
                findUser.getNickname(),
                savedAnswer.getAnswer(),
                savedAnswer.getCreatedAt(),
                savedAnswer.getRecommendationCnt(),
                savedAnswer.getTag(),
                savedAnswer.getSource(),
                savedAnswer.isAi(),
                savedAnswer.isAccepted()
        );

        // 질문 제목 포함한 새로운 DTO 생성
        return new AnswerWithQuestionResponse(answerResponse, findQuestion.getTitle(), communityImg);
    }

    public List<AnswerWithQuestionResponse> getAnswersByUserWithQuestionTitle(AuthUser authUser) {
        List<Answer> answers = answerRepository.findAllByAnswererId(authUser.getId());

        return answers.stream()
                .map(answer -> {
                    Question question = getQuestionById(answer.getQuestionId());
                    String communityImg = getCommunityImageById(question.getCommId());
                    AnswerResponse answerResponse = AnswerResponse.toResponse(
                            answer.getId(),
                            userValidator.getUserOrThrowIfNotExist(authUser.getId()).getNickname(),
                            answer.getAnswer(),
                            answer.getCreatedAt(),
                            answer.getRecommendationCnt(),
                            answer.getTag(),
                            answer.getSource(),
                            answer.isAi(),
                            answer.isAccepted()
                    );
                    return new AnswerWithQuestionResponse(answerResponse, question.getTitle(), communityImg);
                })
                .collect(Collectors.toList());
    }

    private Question getQuestionById(Long qId) {
        return questionRepository.findById(qId).orElseThrow(
                () -> new QuestionInvalidException(ErrorType.QUESTION_NOT_FOUND_ERROR)
        );
    }

    private static Answer buildAnswer(Long qId, AnswerRequest request, Long userId) {
        return Answer.builder()
                .answer(request.getAnswer())
                .tag(request.getTag())
                .source(request.getSource())
                .isAi(false)
                .isAccepted(false)
                .recommendationCnt(0)
                .answererId(userId)
                .questionId(qId)
                .build();
    }

    /**
     * 현재 로그인한 사용자의 질문 목록을 반환.
     */
    @Transactional(readOnly = true)
    public List<MyQuestionResponse> getQuestionsByUser(AuthUser authUser) {
        log.info("Received AuthUser: {}", authUser);

        // 사용자가 작성한 모든 질문 조회
        List<Question> questions = questionRepository.findAllByQuestionerId(authUser.getId());
        log.info("Found questions: {}", questions.size());

        // 질문 목록을 QuestionResponse로 변환
        return questions.stream()
                .map(question -> {
                    String communityImg = getCommunityImageById(question.getCommId());
                    log.info("community img: {}", communityImg);

                    // QuestionResponse 생성 및 반환
                    MyQuestionResponse response = new MyQuestionResponse(
                            question.getTitle(),
                            communityImg
                    );

                    log.info("Response Object: {}", response);
                    return response;

                })
                .collect(Collectors.toList());
    }


    public String getCommunityImageById(Long communityId) {
        // 커뮤니티 이미지 URL 조회, 존재하지 않으면 예외 발생
        return communityRepository.findImgUrlById(communityId)
                .orElseThrow(() -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR));
    }

    public List<MyCommunityResponse> getCommunitiesByUser(Long userId) {
        List<CommunityMember> memberships = communityMemberRepository.findAllByUserId(userId);

        return memberships.stream()
                .map(member -> {
                    Community community = communityRepository.findById(member.getCommunityId())
                            .orElseThrow(() -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR));
                    return MyCommunityResponse.toResponse(community);
                })
                .collect(Collectors.toList());
    }
}
