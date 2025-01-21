package com.hanshin.supernova.validation;

import com.hanshin.supernova.exception.auth.AuthInvalidException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.user.domain.Authority;
import com.hanshin.supernova.user.domain.User;

public class AuthenticationUtils {
    public static void verifyAdmin(User user) {
        if (!user.getAuthority().equals(Authority.ADMIN)) {
            throw new AuthInvalidException(ErrorType.ONLY_ADMIN_AUTHORITY_ERROR);
        }
    }

    public static void verifySameUser(Long comparedUserId, Long originalUserId) {
        if (!comparedUserId.equals(originalUserId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }
}
