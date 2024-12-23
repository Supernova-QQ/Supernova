package com.hanshin.supernova.community.application;

import static com.hanshin.supernova.exception.dto.ErrorType.ONLY_ADMIN_AUTHORITY_ERROR;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.application.AbstractValidateService;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.community.domain.Autority;
import com.hanshin.supernova.community.domain.CommCounter;
import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.domain.CommunityMember;
import com.hanshin.supernova.community.dto.request.CommunityRequest;
import com.hanshin.supernova.community.dto.response.CommunityResponse;
import com.hanshin.supernova.community.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.community.infrastructure.CommunityMemberRepository;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.domain.User;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService extends AbstractValidateService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    /**
     * 커뮤니티 생성
     */
    @Transactional
    public CommunityResponse createCommunity(AuthUser user, CommunityRequest request) {

        // 커뮤니티 이름 중복 체크
        isCommunityNameDuplicated(request);

        // 커뮤니티 저장
        Community community = buildCommunity(
                request, getUserOrThrowIfNotExist(user.getId()).getId());
        Community savedCommunity = communityRepository.save(community);

        // 커뮤니티 생성자 멤버 추가
        CommunityMember savedCommunityMember = buildCommunityMember(savedCommunity,
                Autority.CREATOR, savedCommunity.getCreatedBy());
        communityMemberRepository.save(savedCommunityMember);
        savedCommunity.getCommCounter().increaseMemberCnt();

        return CommunityResponse.toResponse(
                savedCommunity.getId(),
                savedCommunity.getName(),
                savedCommunity.getDescription(),
                savedCommunity.getImgUrl(),
                savedCommunity.getCreatedAt(),
                savedCommunity.isVisible(),
                savedCommunity.isPublic(),
                savedCommunity.isDormant(),
                savedCommunity.getCommCounter()
        );
    }

    /**
     * 커뮤니티 info 수정 - 커뮤니티 생성자만 가능
     */
    @Transactional
    public CommunityResponse updateCommunity(AuthUser user, CommunityRequest request, Long cId) {

        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        // 커뮤니티 생성자 검증
        isCommunityCreator(findCommunity, getUserOrThrowIfNotExist(user.getId()).getId());

        communityInfoUpdate(request, findCommunity);

        return CommunityResponse.toResponse(
                findCommunity.getId(),
                findCommunity.getName(),
                findCommunity.getDescription(),
                findCommunity.getImgUrl(),
                findCommunity.getCreatedAt(),
                findCommunity.isVisible(),
                findCommunity.isPublic(),
                findCommunity.isDormant(),
                findCommunity.getCommCounter()
        );
    }

    /**
     * 커뮤니티 휴면 전환 - 커뮤니티 생성자만 가능
     */
    @Transactional
    public SuccessResponse dormantCommunity(AuthUser user, Long cId) {

        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        isCommunityCreator(findCommunity, getUserOrThrowIfNotExist(user.getId()).getId());

        findCommunity.changeDormant();

        return new SuccessResponse("휴면 전환에 성공하였습니다.");
    }

    /**
     * 커뮤니티 정보 제공
     */
    @Transactional(readOnly = true)
    public CommunityResponse getCommunityInfo(Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);

        return CommunityResponse.toResponse(
                findCommunity.getId(),
                findCommunity.getName(),
                findCommunity.getDescription(),
                findCommunity.getImgUrl(),
                findCommunity.getCreatedAt(),
                findCommunity.isVisible(),
                findCommunity.isPublic(),
                findCommunity.isDormant(),
                findCommunity.getCommCounter()
        );
    }

    /**
     * 커뮤니티 리스트 정보 제공
     */
    @Transactional(readOnly = true)
    public List<CommunityInfoResponse> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();

        return getCommunitySummaryResponseList(communities);
    }

    /**
     * 커뮤니티 목록 - 최신 순
     */
    @Transactional(readOnly = true)
    public Page<CommunityInfoResponse> getLatestCommunities(Pageable pageable) {

        Page<Community> findAllCommunities = communityRepository.findAllOrderByCreatedAtDesc(pageable);

        return findAllCommunities.map(this::convertToCommunityInfoResponse);
    }

    /**
     * 커뮤니티 목록 - 오래된 순
     */
    @Transactional(readOnly = true)
    public Page<CommunityInfoResponse> getOldCommunities(Pageable pageable) {

        Page<Community> findAllCommunities = communityRepository.findAllOrderByCreatedAtASC(pageable);

        return findAllCommunities.map(this::convertToCommunityInfoResponse);
    }

    /**
     * 커뮤니티 가입 요청 처리 - 회원에게 발송된 초대 알림에서 '수락' 버튼을 클릭 시 해당 api 로 요청이 들어온다.
     */
    @Transactional
    public SuccessResponse joinCommunity(AuthUser user, Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);
        // TODO 관리자에게 요청을 보내고, 수락 후 마저 완료되는 비동기 처리 필요
        CommunityMember savedCommunityMember = buildCommunityMember(
                findCommunity, Autority.USER, getUserOrThrowIfNotExist(user.getId()).getId());
        communityMemberRepository.save(savedCommunityMember);
        findCommunity.getCommCounter().increaseMemberCnt();

        return new SuccessResponse("가입 처리 완료");
    }

    /**
     * 회원가입과 동시에 일반 게시판 가입 처리
     */
    @Transactional
    public void joinGeneralCommunity(User user) {
        Community findCommunity = getCommunityOrThrowIfNotExist(1L);
        CommunityMember savedCommunityMember = buildCommunityMember(
                findCommunity, Autority.USER, getUserOrThrowIfNotExist(user.getId()).getId());
        communityMemberRepository.save(savedCommunityMember);
        findCommunity.getCommCounter().increaseMemberCnt();
    }

    /**
     * 커뮤니티 탈퇴
     */
    @Transactional
    public SuccessResponse leaveCommunity(AuthUser user, Long cId) {
        Community findCommunity = getCommunityOrThrowIfNotExist(cId);
        communityMemberRepository.deleteById(
                getUserOrThrowIfNotExist(user.getId()).getId());
        findCommunity.getCommCounter().decreaseMemberCnt();

        return new SuccessResponse("탈퇴 성공");
    }


    private void isCommunityNameDuplicated(CommunityRequest request) {
        if (communityRepository.existsByName(request.getName())) {
            throw new CommunityInvalidException(ErrorType.DUPLICATED_NAME_ERROR);
        }
    }

    private static Community buildCommunity(CommunityRequest request, Long creatorId) {
        CommCounter commCounter = CommCounter.builder()
                .memberCnt(0)
                .questionCnt(0)
                .build();

        return Community.builder()
                .name(request.getName())
                .description(request.getDescription())
                .imgUrl(request.getImgUrl())
                .isVisible(request.isVisible())
                .isPublic(request.isPublic())
                .isDormant(false)
                .createdBy(creatorId)
                .commCounter(commCounter)
                .build();
    }


    private CommunityInfoResponse convertToCommunityInfoResponse(Community community) {
        return new CommunityInfoResponse(
                community.getId(),
                community.getName(),
                community.getCommCounter().getMemberCnt(),
                community.getImgUrl()
        );
    }

    private static CommunityMember buildCommunityMember(Community savedCommunity,
            Autority authority, Long userId) {
        return CommunityMember.builder()
                .communityName(savedCommunity.getName())
                .autority(authority)
                .communityId(savedCommunity.getId())
                .userId(userId)
                .build();
    }

    private static void communityInfoUpdate(CommunityRequest request, Community findCommunity) {
        findCommunity.update(
                request.getName(),
                request.getDescription(),
                request.getImgUrl(),
                request.isVisible(),
                request.isPublic());
    }

    private static void isCommunityCreator(Community findCommunity, Long userId) {
        if (!findCommunity.getCreatedBy().equals(userId)) {
            throw new CommunityInvalidException(ONLY_ADMIN_AUTHORITY_ERROR);
        }
    }

    private static List<CommunityInfoResponse> getCommunitySummaryResponseList(
            List<Community> communities
    ) {
        List<CommunityInfoResponse> communitySummaryResponses = new ArrayList<>();

        communities.forEach(community -> {
            communitySummaryResponses.add(
                    CommunityInfoResponse.toResponse(
                            community.getId(),
                            community.getName(),
                            community.getCommCounter().getMemberCnt(),
                            community.getImgUrl()
                    )
            );
        });
        return communitySummaryResponses;
    }
}
