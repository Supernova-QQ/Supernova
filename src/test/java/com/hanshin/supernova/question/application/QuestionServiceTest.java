package com.hanshin.supernova.question.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.community.domain.CommCounter;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.question.QuestionInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.question.domain.QuestionRecommendation;
import com.hanshin.supernova.question.dto.request.QuestionRequest;
import com.hanshin.supernova.question.dto.response.QuestionResponse;
import com.hanshin.supernova.question.dto.response.QuestionSaveResponse;
import com.hanshin.supernova.question.infrastructure.QuestionRecommendationRepository;
import com.hanshin.supernova.question.infrastructure.QuestionRepository;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import com.hanshin.supernova.validation.CommunityValidator;
import com.hanshin.supernova.validation.QuestionValidator;
import com.hanshin.supernova.validation.UserValidator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private CommunityRepository communityRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CommunityMemberRepository communityMemberRepository;

    @Mock
    private QuestionRecommendationRepository questionRecommendationRepository;

    @Mock
    private AuthUser authUser;

    @Mock
    private User user;

    @InjectMocks
    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        // 1. Mock 객체 초기화
        MockitoAnnotations.openMocks(this);

        // 2. Validator 초기화
        QuestionValidator questionValidator = new QuestionValidator(questionRepository);
        CommunityValidator communityValidator = new CommunityValidator(communityRepository);
        UserValidator userValidator = new UserValidator(userRepository);

        questionService = new QuestionService(
                questionValidator,
                communityValidator,
                userValidator,
                questionRepository,
                communityMemberRepository,
                questionRecommendationRepository
        );

        authUser = () -> 1L;

        user = User.builder()
                .id(1L)
                .nickname("testUser")
                .build();
    }


    @Nested
    @DisplayName("질문 생성 테스트")
    class CreateQuestion {

        private QuestionRequest request;
        private Community community;

        @BeforeEach
        void setUp() {
            request = QuestionRequest.builder()
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .build();

            community = Community.builder()
                    .id(1L)
                    .commCounter(new CommCounter())
                    .build();
        }

        @Test
        @DisplayName("커뮤니티 미선택 시 질문 생성 실패")
        void failWhenCommunityNotFound() {
            // given
            request.setCommId(null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> questionService.createQuestion(authUser, request))
                    .isInstanceOf(CommunityInvalidException.class)
                    .hasMessage(ErrorType.COMMUNITY_NOT_FOUND_ERROR.getMessage());

            verify(questionRepository, never()).save(any(Question.class));
        }

        @Test
        @DisplayName("미인증 시 질문 생성 실패")
        void failWhenUserNotFound() {
            // given
            authUser = () -> null;

            // when & then
            assertThatThrownBy(() -> questionService.createQuestion(authUser, request))
                    .isInstanceOf(UserInvalidException.class)
                    .hasMessage(ErrorType.USER_NOT_FOUND_ERROR.getMessage());

            verify(questionRepository, never()).save(any(Question.class));
        }

        @Test
        @DisplayName("질문 생성 성공")
        void success() {
            // given
            QuestionRequest request = QuestionRequest.builder()
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .build();

            Question question = Question.builder()
                    .id(1L)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .questionerId(1L)
                    .commId(1L)
                    .build();

            // when
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(questionRepository.save(any(Question.class))).thenReturn(question);

            // then
            // 저장된 데이터 일치 확인
            QuestionSaveResponse response = questionService.createQuestion(authUser, request);
            assertThat(response.getTitle()).isEqualTo(request.getTitle());
            assertThat(response.getContent()).isEqualTo(request.getContent());
            assertThat(response.getCommId()).isEqualTo(request.getCommId());

            // 커뮤니티 질문 수 증가 확인
            assertThat(community.getCommCounter().getQuestionCnt()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("질문 조회 테스트")
    class GetQuestion {

        @Test
        @DisplayName("존재하지 않는 질문 조회 실패")
        void failWhenQuestionNotFound() {
            // given
            when(questionRepository.findById(999L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> questionService.getQuestion(999L))
                    .isInstanceOf(QuestionInvalidException.class)
                    .hasMessageContaining(ErrorType.QUESTION_NOT_FOUND_ERROR.getMessage());
        }

        @Test
        @DisplayName("질문 조회 성공")
        void successCreateQuestion() {
            // given
            QuestionRequest request = QuestionRequest.builder()
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .build();

            Community community = Community.builder()
                    .id(1L)
                    .commCounter(new CommCounter())
                    .build();

            User user = User.builder()
                    .id(1L)
                    .nickname("user")
                    .build();

            Question question = Question.builder()
                    .id(1L)
                    .title(request.getTitle())
                    .content(request.getContent())
                    .questionerId(1L)
                    .commId(1L)
                    .build();

            // when
            when(questionRepository.findById(1L)).thenReturn(Optional.of(question));
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // then
            QuestionResponse getResponse = questionService.getQuestion(question.getId());
            assertThat(getResponse.getTitle()).isEqualTo(question.getTitle());
            assertThat(getResponse.getContent()).isEqualTo(question.getContent());
            assertThat(getResponse.getCommId()).isEqualTo(question.getCommId());
        }
    }

    @Nested
    @DisplayName("질문 수정 테스트")
    class editQuestion {

        private Question existingQuestion;
        private QuestionRequest request;
        private Community community;
        private Community changedCommunity;

        @BeforeEach
        void setUp() {
            existingQuestion = Question.builder()
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .questionerId(1L)
                    .build();

            request = QuestionRequest.builder()
                    .title("changedTitle")
                    .content("changedContent")
                    .commId(2L)
                    .build();

            community = Community.builder()
                    .id(1L)
                    .commCounter(CommCounter.builder()
                            .questionCnt(1)
                            .build())
                    .build();

            changedCommunity = Community.builder()
                    .id(2L)
                    .commCounter(new CommCounter())
                    .build();
        }

        @Test
        @DisplayName("미인증 시 질문 수정 실패")
        void failWhenUserNotFound() {
            // given
            authUser = () -> null;
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));

            // when & then
            assertThatThrownBy(() -> questionService.editQuestion(authUser, 1L, request))
                    .isInstanceOf(UserInvalidException.class)
                    .hasMessage(ErrorType.USER_NOT_FOUND_ERROR.getMessage());
        }

        @Test
        @DisplayName("작성자 불일치 시 질문 수정 실패")
        void failWhenNonIdenticalUser() {
            // given
            authUser = () -> 2L;
            User accessUser = User.builder()
                    .id(2L)
                    .nickname("accessUser")
                    .build();

            when(userRepository.findById(authUser.getId())).thenReturn(Optional.of(accessUser));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));

            // then
            assertThatThrownBy(() -> questionService.editQuestion(authUser, 1L, request))
                    .isInstanceOf(AuthInvalidException.class)
                    .hasMessageContaining(ErrorType.NON_IDENTICAL_USER_ERROR.getMessage());
        }

        @Test
        @DisplayName("존재하지 않는 질문 수정 실패")
        void failWhenQuestionNotFound() {
            // given
            when(questionRepository.findById(2L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> questionService.editQuestion(authUser, 2L, request))
                    .isInstanceOf(QuestionInvalidException.class)
                    .hasMessageContaining(ErrorType.QUESTION_NOT_FOUND_ERROR.getMessage());
        }

        @Test
        @DisplayName("커뮤니티 미선택 시 질문 수정 실패")
        void failWhenCommunityNotFound() {
            // given
            request.setCommId(null);
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> questionService.editQuestion(authUser, 1L, request))
                    .isInstanceOf(CommunityInvalidException.class)
                    .hasMessage(ErrorType.COMMUNITY_NOT_FOUND_ERROR.getMessage());
        }

        @Test
        @DisplayName("질문 수정 성공")
        void successEditQuestion() {
            // given
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));
            when(communityRepository.findById(2L)).thenReturn(Optional.of(changedCommunity));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when
            questionService.editQuestion(authUser, 1L, request);

            // then
            // 데이터 변경 확인
            assertThat(existingQuestion.getTitle()).isEqualTo("changedTitle");
            assertThat(existingQuestion.getContent()).isEqualTo("changedContent");
            // 커뮤니티 질문 수 증감 확인
            assertThat(community.getCommCounter().getQuestionCnt()).isEqualTo(0);
            assertThat(changedCommunity.getCommCounter().getQuestionCnt()).isEqualTo(1);
//            verify(questionRepository, times(1)).save(any(Question.class));   // JPA 영속성 컨텍스트 때문에, 엔티티의 변경사항은 트랜잭션 커밋 시 자동으로 DB에 반영된다(더티 체킹). 따라서 save 호출을 검증할 필요가 없다.
        }
    }

    @Nested
    @DisplayName("질문 삭제 테스트")
    class deleteQuestion {

        private Question existingQuestion;
        private Community community;

        @BeforeEach
        void setUp() {
            existingQuestion = Question.builder()
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .questionerId(1L)
                    .build();

            community = Community.builder()
                    .id(1L)
                    .commCounter(CommCounter.builder()
                            .questionCnt(1)
                            .build())
                    .build();
        }

        @Test
        @DisplayName("미인증 시 질문 삭제 실패")
        void failWhenUserNotFound() {
            // given
            authUser = () -> null;
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));

            // when & then
            assertThatThrownBy(() -> questionService.deleteQuestion(authUser, 1L))
                    .isInstanceOf(UserInvalidException.class)
                    .hasMessage(ErrorType.USER_NOT_FOUND_ERROR.getMessage());

            verify(questionRepository, never()).deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 질문 삭제 실패")
        void failWhenQuestionNotFound() {
            // given
            when(questionRepository.findById(2L)).thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> questionService.deleteQuestion(authUser, 2L))
                    .isInstanceOf(QuestionInvalidException.class)
                    .hasMessageContaining(ErrorType.QUESTION_NOT_FOUND_ERROR.getMessage());
            verify(questionRepository, never()).delete(existingQuestion);
        }

        @Test
        @DisplayName("작성자 불일치 시 질문 삭제 실패")
        void failWhenNonIdenticalUser() {
            // given
            authUser = () -> 2L;
            User accessUser = User.builder()
                    .id(2L)
                    .nickname("accessUser")
                    .build();
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(userRepository.findById(2L)).thenReturn(Optional.of(accessUser));

            // when & then
            assertThatThrownBy(() -> questionService.deleteQuestion(authUser, 1L))
                    .isInstanceOf(AuthInvalidException.class)
                    .hasMessageContaining(ErrorType.NON_IDENTICAL_USER_ERROR.getMessage());
            verify(questionRepository, never()).delete(existingQuestion);
        }

        @Test
        @DisplayName("질문 삭제 성공")
        void successDeleteQuestion() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));

            // when
            questionService.deleteQuestion(authUser, 1L);

            // then
            assertThat(community.getCommCounter().getQuestionCnt()).isEqualTo(0);
            verify(questionRepository).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("질문 추천 테스트")
    class recommendQuestion {
        private Question existingQuestion;
        private Community community;

        @BeforeEach
        void setUp() {
            existingQuestion = Question.builder()
                    .id(1L)
                    .title("title")
                    .content("content")
                    .commId(1L)
                    .questionerId(1L)
                    .recommendationCnt(1)
                    .build();

            community = Community.builder()
                    .id(1L)
                    .commCounter(CommCounter.builder()
                            .questionCnt(1)
                            .build())
                    .build();
        }

        @Test
        @DisplayName("미인증 시 질문 추천 실패")
        void failWhenUserNotFound() {
            // given
            authUser = () -> null;

            // when & then
            assertThatThrownBy(() -> questionService.updateQuestionRecommendation(authUser, 1L))
                    .isInstanceOf(UserInvalidException.class)
                    .hasMessage(ErrorType.USER_NOT_FOUND_ERROR.getMessage());

            assertThat(existingQuestion.getRecommendationCnt()).isEqualTo(1);

            verify(questionRecommendationRepository, never()).save(any(QuestionRecommendation.class));
        }

        @Test
        @DisplayName("존재하지 않는 질문 추천 실패")
        void failWhenQuestionNotFound() {
            // given
            when(questionRepository.findById(2L)).thenReturn(Optional.empty());
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));

            // when & then
            assertThatThrownBy(() -> questionService.updateQuestionRecommendation(authUser, 2L))
                    .isInstanceOf(QuestionInvalidException.class)
                    .hasMessageContaining(ErrorType.QUESTION_NOT_FOUND_ERROR.getMessage());

            assertThat(existingQuestion.getRecommendationCnt()).isEqualTo(1);

            verify(questionRecommendationRepository, never()).save(any(QuestionRecommendation.class));
        }

        @Test
        @DisplayName("작성자 일치 시 질문 추천 실패")
        void failWhenNonIdenticalUser() {
            // given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));

            // when & then
            assertThatThrownBy(() -> questionService.updateQuestionRecommendation(authUser, 1L))
                    .isInstanceOf(AuthInvalidException.class)
                    .hasMessageContaining(ErrorType.WRITER_CANNOT_RECOMMEND_ERROR.getMessage());

            assertThat(existingQuestion.getRecommendationCnt()).isEqualTo(1);

            verify(questionRecommendationRepository, never()).save(any(QuestionRecommendation.class));
        }

        @Test
        @DisplayName("기존 추천 이력 없을 시 질문 추천 성공")
        void successRecommendQuestion() {
            // given
            when(questionRecommendationRepository.findByQuestionIdAndRecommenderId(any(),
                    any())).thenReturn(Optional.empty());
            authUser = () -> 2L;
            User accessUser = User.builder()
                    .id(2L)
                    .nickname("accessUser")
                    .build();
            when(userRepository.findById(2L)).thenReturn(Optional.of(accessUser));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));

            // when
            questionService.updateQuestionRecommendation(authUser, 1L);

            // then
            assertThat(existingQuestion.getRecommendationCnt()).isEqualTo(2);

            verify(questionRecommendationRepository).save(any(QuestionRecommendation.class));
        }

        @Test
        @DisplayName("기존 추천 이력 존재 시 질문 추천 취소 성공")
        void successUnRecommendQuestion() {
            // given
            QuestionRecommendation questionRecommendation = QuestionRecommendation.builder()
                    .id(1L)
                    .questionId(1L)
                    .recommenderId(2L)
                    .build();
            when(questionRecommendationRepository.findByQuestionIdAndRecommenderId(1L,
                    2L)).thenReturn(Optional.of(questionRecommendation));
            authUser = () -> 2L;
            User accessUser = User.builder()
                    .id(2L)
                    .nickname("accessUser")
                    .build();
            when(userRepository.findById(2L)).thenReturn(Optional.of(accessUser));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(questionRepository.findById(1L)).thenReturn(Optional.of(existingQuestion));
            when(communityRepository.findById(1L)).thenReturn(Optional.of(community));

            // when
            questionService.updateQuestionRecommendation(authUser, 1L);

            // then
            assertThat(existingQuestion.getRecommendationCnt()).isEqualTo(0);

            verify(questionRecommendationRepository).deleteById(1L);
        }
    }
}

