package com.hanshin.supernova.security.argumentResolver;

import com.hanshin.supernova.auth.model.AuthUser;
import com.hanshin.supernova.auth.model.AuthUserImpl;
import com.hanshin.supernova.exception.auth.AuthorizationException;
import com.hanshin.supernova.exception.dto.ErrorType;
import com.hanshin.supernova.security.service.JwtService;
import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthUserResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;

    @Autowired
    public AuthUserResolver(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthUserImpl.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final String BEARER = "Bearer ";

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String accessToken = request.getHeader(HttpHeaderNames.AUTHORIZATION.toString());
        if (accessToken == null || !accessToken.startsWith(BEARER)) {
            return null;
        }

        Long userId = jwtService.getUserId(accessToken.substring(BEARER.length()));
        return new AuthUserImpl(userId);
    }

    public AuthUser getAuthUser(String token) {
        final String BEARER = "Bearer ";

        if (token == null || !token.startsWith(BEARER)) {
            throw new AuthorizationException(ErrorType.AUTHORIZATION_ERROR);
        }
        Long userId = jwtService.getUserId(token.substring(BEARER.length()));
        return new AuthUserImpl(userId);
    }

}