package com.hanshin.supernova.notice.application;

import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.notice.NoticeInvalidException;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.notice.domain.Notice;
import com.hanshin.supernova.notice.dto.request.NoticeRequest;
import com.hanshin.supernova.notice.dto.response.NoticeResponse;
import com.hanshin.supernova.notice.infrastructure.NoticeRepository;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeResponse createNotice(Long userId, NoticeRequest request) {
        log.info("Creating notice for user {}", userId);
        validateAuthority(userId);
        Notice notice = buildNotice(request);
        Notice savedNotice = noticeRepository.save(notice);
        return getNoticeResponse(savedNotice);
    }

    /**
     * 공지사항 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeId) {
        Notice findNotice = getOrThrowsIfNoticeNotExist(noticeId);
        return getNoticeResponse(findNotice);
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeResponse updateNotice(Long userId, Long noticeId, NoticeRequest request) {
        validateAuthority(userId);
        Notice findNotice = getOrThrowsIfNoticeNotExist(noticeId);
        findNotice.update(request.getTitle(), request.getContent(), request.isPinned());
        return getNoticeResponse(findNotice);
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public SuccessResponse deleteNotice(Long userId, Long noticeId) {
        validateAuthority(userId);
        Notice findNotice = getOrThrowsIfNoticeNotExist(noticeId);
        noticeRepository.deleteById(findNotice.getId());
        return new SuccessResponse("공지사항 삭제를 성공했습니다.");
    }


    private static Notice buildNotice(NoticeRequest request) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .isPinned(request.isPinned())
                .build();
    }

    // 관리자 권한을 가진 유저만 공지사항을 생성/수정/삭제 가능하다.
    private void validateAuthority(Long userId) {
        User findUser = userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );

        if (!findUser.getAuthority().equals(Authority.ADMIN)) {
            throw new AuthInvalidException(ErrorType.ONLY_ADMIN_AUTHORITY_ERROR);
        }
    }

    private Notice getOrThrowsIfNoticeNotExist(Long noticeId) {
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