package com.hanshin.supernova.hashtag.application;

import static com.hanshin.supernova.hashtag.HashtagConstants.QUESTION_HASHTAG_MAX_SIZE;

import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.hashtag.HashtagInvalidException;
import com.hanshin.supernova.hashtag.domain.Hashtag;
import com.hanshin.supernova.hashtag.domain.QuestionHashtag;
import com.hanshin.supernova.hashtag.dto.request.HashtagRequest;
import com.hanshin.supernova.hashtag.dto.response.HashtagSaveResponse;
import com.hanshin.supernova.hashtag.infrastructure.HashtagRepository;
import com.hanshin.supernova.hashtag.infrastructure.QuestionHashtagRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final QuestionHashtagRepository questionHashtagRepository;

    @Transactional
    public HashtagSaveResponse saveQuestionHashtag(HashtagRequest request) {

        Long qId = request.getQuestionId();
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
}
