package com.hanshin.supernova.common.application;

import com.hanshin.supernova.community.domain.Community;
import com.hanshin.supernova.community.infrastructure.CommunityRepository;
import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.community.CommunityInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.exception.user.UserInvalidException;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;
import com.hanshin.supernova.user.infrastructure.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractValidateService {

    protected UserRepository userRepository;
    protected CommunityRepository communityRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    protected User getUserOrThrowIfNotExist(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

    protected Community getCommunityOrThrowIfNotExist(Long cId) {
        return communityRepository.findById(cId).orElseThrow(
                () -> new CommunityInvalidException(ErrorType.COMMUNITY_NOT_FOUND_ERROR)
        );
    }

    protected static void verifyAdmin(User user) {
        if (!user.getAuthority().equals(Authority.ADMIN)) {
            throw new AuthInvalidException(ErrorType.ONLY_ADMIN_AUTHORITY_ERROR);
        }
    }
}
