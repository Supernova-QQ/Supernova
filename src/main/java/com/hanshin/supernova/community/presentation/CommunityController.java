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

    @PostMapping
    public ResponseEntity<?> createCommunity(
            AuthUser user,
            @RequestBody @Valid CommunityRequest request) {
        var response = communityService.createCommunity(user, request);
        return ResponseDto.created(response);
    }

    @PutMapping(path = "/{c_id}")
    public ResponseEntity<?> updateCommunity(
            AuthUser user,
            @RequestBody @Valid CommunityRequest request,
            @PathVariable(name = "c_id") Long cId
    ) {
        var response = communityService.updateCommunity(user, request, cId);
        return ResponseDto.ok(response);
    }

    @PatchMapping(path = "/{c_id}")
    public ResponseEntity<?> dormantCommunity(
            AuthUser user,
            @PathVariable(name = "c_id") Long cId
    ) {
        var response = communityService.dormantCommunity(user, cId);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{c_id}")
    public ResponseEntity<?> getCommunityInfo(
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

    @GetMapping(path = "/all-latest-communities")
    public ResponseEntity<?> allLatestCommunities(
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<CommunityInfoResponse> response = communityService.getLatestCommunities(pageable);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/all-old-communities")
    public ResponseEntity<?> allOldCommunities(
            @PageableDefault(size = 7) Pageable pageable
    ) {
        Page<CommunityInfoResponse> response = communityService.getOldCommunities(pageable);
        return ResponseDto.ok(response);
    }

    @PostMapping(path = "/{c_id}")
    public ResponseEntity<?> joinCommunity(
            AuthUser user,
            @PathVariable(name = "c_id") Long cId
    ) {
        SuccessResponse response = communityService.joinCommunity(user, cId);
        return ResponseDto.created(response);
    }

    @DeleteMapping(path = "/{c_id}")
    public ResponseEntity<?> LeaveCommunity(
            AuthUser user,
            @PathVariable(name = "c_id") Long cId
    ) {
        SuccessResponse response = communityService.leaveCommunity(user, cId);
        return ResponseDto.ok(response);
    }

}
