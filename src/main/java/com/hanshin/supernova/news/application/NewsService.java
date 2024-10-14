package com.hanshin.supernova.news.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.news.NewsInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.news.domain.News;
import com.hanshin.supernova.news.dto.request.NewsRequest;
import com.hanshin.supernova.news.dto.response.NewsResponse;
import com.hanshin.supernova.news.infrastructure.NewsRepository;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService extends AbstractValidateService {

    private final NewsRepository newsRepository;
    private final UserRepository userRepository;

    public NewsResponse createNews(NewsRequest request) {
        getUserOrThrowIfNotExist(request.getReceiverId());

        News savedNews = buildAndSaveNews(request);

        return NewsResponse.toResponse(
                savedNews.getId(), savedNews.getTitle(), savedNews.getContent(),
                savedNews.getType(), savedNews.isViewed(), savedNews.getCreatedAt(),
                savedNews.isHasRelatedContent(), savedNews.getRelatedContentId());
    }

    public NewsResponse getNews(AuthUser user, Long newsId) {
        News findNews = getNewsOrThrowIfNotExist(newsId);

        // 알림에 접근하는 사용자와 알림 수신자가 일치하는지 검증
        verifyReceiver(user.getId(), findNews);

        // 조회한 적 없는 알림의 경우 조회한 상태로 변경
        if (!findNews.isViewed()) {
            findNews.changeViewed();
        }

        return NewsResponse.toResponse(
                findNews.getId(), findNews.getTitle(), findNews.getContent(),
                findNews.getType(), findNews.isViewed(), findNews.getCreatedAt(),
                findNews.isHasRelatedContent(), findNews.getRelatedContentId()
        );
    }

    public NewsResponse updateNews(AuthUser user, Long newsId, NewsRequest request) {
        News findNews = getNewsOrThrowIfNotExist(newsId);

        User findUser = getUserOrThrowIfNotExist(user.getId());

        // 관리자 권한 검증
        verifyAdmin(findUser);

        findNews.update(request.getTitle(), request.getContent(), request.getType(),
                request.isHasRelatedContent(), request.getRelatedContentId());

        return NewsResponse.toResponse(
                findNews.getId(), findNews.getTitle(), findNews.getContent(),
                findNews.getType(), findNews.isViewed(), findNews.getCreatedAt(),
                findNews.isHasRelatedContent(), findNews.getRelatedContentId()
        );
    }

    public SuccessResponse deleteNews(AuthUser user, Long newsId) {
        User findUser = getUserOrThrowIfNotExist(user.getId());

        verifyAdmin(findUser);

        newsRepository.deleteById(newsId);

        return new SuccessResponse("알림 삭제 완료");
    }

    public List<NewsResponse> getUnViewedNews(AuthUser user) {
        List<News> findNewsList = newsRepository.findAllByReceiverIdAndIsViewedFalseOrderByCreatedAtDesc(
                user.getId());

        return getNewsResponses(findNewsList);
    }

    public List<NewsResponse> getViewedNews(AuthUser user) {
        List<News> findNewsList = newsRepository.findAllByReceiverIdAndIsViewedTrueOrderByCreatedAtDesc(
                user.getId());

        return getNewsResponses(findNewsList);
    }


    private News getNewsOrThrowIfNotExist(Long newsId) {
        return newsRepository.findById(newsId).orElseThrow(
                () -> new NewsInvalidException(ErrorType.NEWS_NOT_FOUND_ERROR));
    }

    private static void verifyReceiver(Long userId, News findNews) {
        if (!findNews.getReceiverId().equals(userId)) {
            throw new UserInvalidException(ErrorType.NOT_RECEIVER_ERROR);
        }
    }

    private News buildAndSaveNews(NewsRequest request) {
        User systemUser = userRepository.findByAuthority(Authority.SYSTEM)
                .orElseThrow(() -> new NewsInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR));

        News news = News.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .type(request.getType())
                .isViewed(false)
                .senderId(systemUser.getId())
                .receiverId(request.getReceiverId())
                .hasRelatedContent(request.isHasRelatedContent())
                .relatedContentId(request.getRelatedContentId())
                .build();
        return newsRepository.save(news);
    }

    private List<NewsResponse> getNewsResponses(List<News> findNewsList) {
        List<NewsResponse> responseList = new ArrayList<>();
        findNewsList.forEach(news -> {
            responseList.add(
                    NewsResponse.toResponse(news.getId(), news.getTitle(), news.getContent(),
                            news.getType(), news.isViewed(), news.getCreatedAt(),
                            news.isHasRelatedContent(), news.getRelatedContentId())
            );
        });

        return responseList;
    }
}
