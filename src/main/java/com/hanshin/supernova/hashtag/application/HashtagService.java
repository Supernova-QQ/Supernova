package com.hanshin.supernova.hashtag.application;

import static com.hanshin.supernova.hashtag.HashtagConstants.QUESTION_HASHTAG_MAX_SIZE;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.hashtag.HashtagInvalidException;
import com.hanshin.supernova.hashtag.domain.Hashtag;
import com.hanshin.supernova.hashtag.domain.QuestionHashtag;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import com.hanshin.supernova.hashtag.dto.response.HashtagSaveResponse;
import com.hanshin.supernova.hashtag.infrastructure.HashtagRepository;
import com.hanshin.supernova.hashtag.infrastructure.QuestionHashtagRepository;
import com.hanshin.supernova.question.domain.Question;
import com.hanshin.supernova.validation.QuestionValidator;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashtagService {
    private final QuestionValidator questionValidator;

    private final HashtagRepository hashtagRepository;
    private final QuestionHashtagRepository questionHashtagRepository;
    private final HttpServletRequest request;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public HashtagSaveResponse saveQuestionHashtag(Long qId, HashtagRequest request,
                                                   AuthUser authUser) {

        List<String> hashtagNames = request.getHashtagNames();
        List<String> savedHashtagNames = new ArrayList<>();

        if (hashtagNames.size() > QUESTION_HASHTAG_MAX_SIZE) {
            throw new HashtagInvalidException(ErrorType.HASHTAG_MAX_SIZE_5_ERROR);
        }
        questionHashtagRepository.deleteAllByQuestionId(qId);

        hashtagNames.forEach(hashtagName -> {
            Hashtag hashtag;

            // 해시태그가 기존에 존재하지 않으면 생성, 존재하면 찾아온다.
            if (!hashtagRepository.existsByName(hashtagName)) {
                Hashtag newHashtag = Hashtag.builder()
                        .name(hashtagName)
                        .build();
                hashtag = hashtagRepository.save(newHashtag);
            } else {
                hashtag = hashtagRepository.findByName(hashtagName);
            }

            // 질문 해시태그를 생성한다.
            QuestionHashtag questionHashtag = QuestionHashtag.builder()
                    .questionId(qId)
                    .hashtagId(hashtag.getId())
                    .build();

            questionHashtagRepository.save(questionHashtag);
            savedHashtagNames.add(hashtag.getName());

            // redis 에 해시태그 사용(저장, 이용) 기록 저장
            recordTaggingData(hashtag.getId(), authUser);
        });

        return HashtagSaveResponse.toResponse(savedHashtagNames);
    }

    @Transactional(readOnly = true)
    public List<String> getHashtagNames(Long qId) {
        List<String> hashtagNames = new ArrayList<>();

        List<QuestionHashtag> questionHashtags = questionHashtagRepository.findByQuestionId(qId);
        questionHashtags.forEach(questionHashtag -> {
            Hashtag findHashtag = hashtagRepository.findById(questionHashtag.getHashtagId())
                    .orElseThrow(
                            () -> new HashtagInvalidException(ErrorType.HASHTAG_NOT_FOUND_ERROR)
                    );
            hashtagNames.add(findHashtag.getName());
        });

        return hashtagNames;
    }


    private void recordTaggingData(Long hashtagId, AuthUser authUser) {
        String taggerIdentifier =
                (authUser != null) ? authUser.getId().toString() : request.getRemoteAddr();

        // IPv6 주소 처리: IPv6 주소의 ':' 를 '_'로 대체하여 Redis 키 구분자와 충돌 방지
        if (taggerIdentifier.contains(":")) {
            taggerIdentifier = taggerIdentifier.replace(":", "_");
        }

        String userAgent = request.getHeader("User-Agent");
        String today = LocalDate.now().toString();
        String key = "hashtag:" + hashtagId + ":tagging:" + taggerIdentifier + ":" + today;

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();

        // redis 에 해시태그 사용 정보 저장
        valueOperations.set(key, userAgent);
        log.debug("New Hashtag using recorded: hashtagId={}, taggerIdentifier={}",
                hashtagId, taggerIdentifier);
    }

    // 해시태그 검색을 위한 메소드
    @Transactional(readOnly = true)
    public List<Question> getQuestionsByHashtagName(String hashtagName) {
        List<Question> findQuestions = new ArrayList<>();

        // 'hashtagName' 를 포함한 해시태그 목록 조회
        List<Hashtag> hashtagsByName = hashtagRepository.findByNameContaining(hashtagName);
        if(hashtagsByName == null) {
            return null;
        }

        List<QuestionHashtag> questionHashtags = new ArrayList<>();
        // 각 해시태그(hashtagsByName)가 포함된 질문 해시태그 조회
        hashtagsByName.forEach(
                hashtag -> questionHashtags.addAll(
                        questionHashtagRepository.findByHashtagId(hashtag.getId()))
        );

        questionHashtags.forEach(
                questionHashtag -> findQuestions.add(
                        questionValidator.getQuestionOrThrowIfNotExist(questionHashtag.getQuestionId()))
        );

        return findQuestions;
    }
}
