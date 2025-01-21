package com.hanshin.supernova.validation;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityValidator {
    private final CommunityRepository communityRepository;

    public Community getCommunityOrThrowIfNotExist(Long cId) {
        return communityRepository.findById(cId).orElseThrow(
                () -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR)
        );
    }
}
