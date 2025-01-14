package com.hanshin.supernova.community.presentation;

import static com.hanshin.supernova.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.community.application.CommunityService;
import com.hanshin.supernova.community.dto.request.CommunityRequest;
import com.hanshin.supernova.community.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.my.dto.response.MyCommunityResponse;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 생성")
    @PostMapping
    public ResponseEntity<?> createCommunity(
            AuthUser user,
            @Parameter(required = true, description = "커뮤니티 생성 요청")
            @RequestBody @Valid CommunityRequest request) {
        var response = communityService.createCommunity(user, request);
        return ResponseDto.created(response);
    }

    @Operation(summary = "커뮤니티 수정")
    @PutMapping(path = "/{c_id}")
    public ResponseEntity<?> updateCommunity(
            AuthUser user,
            @Parameter(required = true, description = "커뮤니티 수정 요청")
            @RequestBody @Valid CommunityRequest request,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long cId
    ) {
        var response = communityService.updateCommunity(user, request, cId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "커뮤니티 휴면 전환")
    @PatchMapping(path = "/{c_id}")
    public ResponseEntity<?> dormantCommunity(
            AuthUser user,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long cId
    ) {
        var response = communityService.dormantCommunity(user, cId);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "커뮤니티 정보 조회")
    @GetMapping(path = "/{c_id}")
    public ResponseEntity<?> getCommunityInfo(
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long cId
    ) {
        var response = communityService.getCommunityInfo(cId);
        return ResponseDto.ok(response);
    }

//    @GetMapping
//    public ResponseEntity<?> getAllCommunities() {
//        List<CommunityInfoResponse> response = communityService.getAllCommunities();
//        return ResponseDto.ok(response);
//    }

    @Operation(summary = "최신 등록 순 커뮤니티 목록 조회")
    @GetMapping(path = "/all-latest-communities")
    public ResponseEntity<?> allLatestCommunities(
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<CommunityInfoResponse> response = communityService.getLatestCommunities(pageable);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "오래된 순 커뮤니티 목록 조회")
    @GetMapping(path = "/all-old-communities")
    public ResponseEntity<?> allOldCommunities(
            @Parameter(description = "한 페이지의 데이터 개수")
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<CommunityInfoResponse> response = communityService.getOldCommunities(pageable);
        return ResponseDto.ok(response);
    }

    @Operation(summary = "커뮤니티 가입")
    @PostMapping(path = "/{c_id}")
    public ResponseEntity<?> joinCommunity(
            AuthUser user,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long cId
    ) {
        SuccessResponse response = communityService.joinCommunity(user, cId);
        return ResponseDto.created(response);
    }

    @Operation(summary = "커뮤니티 탈퇴")
    @DeleteMapping(path = "/{c_id}")
    public ResponseEntity<?> LeaveCommunity(
            AuthUser user,
            @Parameter(description = "커뮤니티 고유 번호")
            @PathVariable(name = "c_id") Long cId
    ) {
        SuccessResponse response = communityService.leaveCommunity(user, cId);
        return ResponseDto.ok(response);
    }

}
