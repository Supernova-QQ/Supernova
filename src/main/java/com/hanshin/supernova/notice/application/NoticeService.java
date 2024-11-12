package com.hanshin.supernova.notice.application;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.notice.NoticeInvalidException;
import com.hanshin.supernova.notice.domain.Notice;
import com.hanshin.supernova.notice.dto.request.NoticeRequest;
import com.hanshin.supernova.notice.dto.response.NoticeResponse;
import com.hanshin.supernova.notice.infrastructure.NoticeRepository;
import com.hanshin.supernova.user.domain.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService extends AbstractValidateService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeResponse createNotice(AuthUser user, NoticeRequest request) {
        log.info("Creating notice for user {}", user.getId());
        User findUser = getUserOrThrowIfNotExist(user.getId());
        verifyAdmin(findUser);
        Notice notice = buildNotice(request);
        Notice savedNotice = noticeRepository.save(notice);
        return getNoticeResponse(savedNotice);
    }

    /**
     * 공지사항 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeId) {
        Notice findNotice = getNoticeOrThrowsIfNoticeNotExist(noticeId);
        return getNoticeResponse(findNotice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeResponse updateNotice(AuthUser user, Long noticeId, NoticeRequest request) {
        User findUser = getUserOrThrowIfNotExist(user.getId());
        verifyAdmin(findUser);
        Notice findNotice = getNoticeOrThrowsIfNoticeNotExist(noticeId);
        findNotice.update(request.getTitle(), request.getContent(), request.isPinned());
        return getNoticeResponse(findNotice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public SuccessResponse deleteNotice(AuthUser user, Long noticeId) {
        User findUser = getUserOrThrowIfNotExist(user.getId());
        verifyAdmin(findUser);
        Notice findNotice = getNoticeOrThrowsIfNoticeNotExist(noticeId);
        noticeRepository.deleteById(findNotice.getId());
        return new SuccessResponse("공지사항 삭제를 성공했습니다.");
    }

    /**
     * 공지사항 목록 최신순
     */
    public List<NoticeResponse> getAllNotices() {
        List<Notice> notices = noticeRepository.findAllByOrderByCreatedAtDesc();
        List<NoticeResponse> responses = new ArrayList<>();
        notices.forEach(notice -> {
            responses.add(getNoticeResponse(notice));
        });
        return responses;
    }

    /**
     * 고정된 공지사항 목록
     */
    public List<NoticeResponse> getPinnedNotices() {
        List<Notice> notices = noticeRepository.findPinnedNotices();
        List<NoticeResponse> responses = new ArrayList<>();
        notices.forEach(notice -> {
            responses.add(getNoticeResponse(notice));
        });
        return responses;
    }


    private static Notice buildNotice(NoticeRequest request) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.isPinned())
                .build();
    }


    private Notice getNoticeOrThrowsIfNoticeNotExist(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(
                () -> new NoticeInvalidException(ErrorType.NOTICE_NOT_FOUND_ERROR));
    }

    private static NoticeResponse getNoticeResponse(Notice savedNotice) {
        return NoticeResponse.toResponse(
                savedNotice.getId(),
                savedNotice.getTitle(),
                savedNotice.getContent(),
                savedNotice.getCreatedAt(),
                savedNotice.isPinned()
        );
    }
}