package com.hanshin.supernova.community.presentation;

import com.hanshin.supernova.common.dto.SuccessResponse;
import com.hanshin.supernova.common.model.ResponseDto;
import com.hanshin.supernova.community.application.CommunityService;
import com.hanshin.supernova.community.dto.request.CommunityRequest;
import com.hanshin.supernova.community.dto.response.CommunityInfoResponse;
import com.hanshin.supernova.community.dto.response.CommunityResponse;
import com.hanshin.supernova.community.dto.response.CommunitySummaryResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @PostMapping
    public ResponseEntity<?> createCommunity(
            @RequestBody @Valid CommunityRequest request) {
        CommunityResponse response = communityService.createCommunity(request);
        return ResponseDto.created(response);
    }

    @PutMapping(path = "/{c_id}")
    public ResponseEntity<?> updateCommunity(
            @RequestBody @Valid CommunityRequest request,
            @PathVariable(name = "c_id") Long cId
    ) {
        CommunityResponse response = communityService.updateCommunity(request, cId);
        return ResponseDto.ok(response);
    }

    @PatchMapping(path = "/{c_id}")
    public ResponseEntity<?> dormantCommunity(
            @PathVariable(name = "c_id") Long cId
    ) {
        SuccessResponse response = communityService.dormantCommunity(cId);
        return ResponseDto.ok(response);
    }

    @GetMapping(path = "/{c_id}")
    public ResponseEntity<?> getCommunityInfo(
            @PathVariable(name = "c_id") Long cId
    ) {
        CommunityInfoResponse response = communityService.getCommunityInfo(cId);
        return ResponseDto.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getAllCommunities() {
        List<CommunitySummaryResponse> response = communityService.getAllCommunities();
        return ResponseDto.ok(response);
    }
}
