package com.hanshin.supernova.config.resolver;

import com.hanshin.supernova.auth.v2.application.SecurityTokenService;
import com.hanshin.supernova.auth.v2.model.SecurityAuthToken;
import com.hanshin.supernova.auth.v2.model.SecurityAuthUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static com.hanshin.supernova.auth.AuthCostants.AUTH_TOKEN_HEADER_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    private final SecurityTokenService tokenService;

    @Override
    public boolean supportsParameter(
            MethodParameter parameter) {
        return parameter.getParameterType()
                .equals(SecurityAuthUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        var httpServletRequest = (HttpServletRequest) webRequest.getNativeRequest();

        var accessToken = httpServletRequest.getHeader(
                AUTH_TOKEN_HEADER_KEY);

        log.info(httpServletRequest.getHeader(AUTH_TOKEN_HEADER_KEY));

        if (accessToken
                == null) {
            if (parameter.isOptional()) {
                return null;
            }
            log.error("토큰 없음 2");
            accessToken = "";
        }

        var token = new SecurityAuthToken(accessToken);

        return tokenService.getAuthUser(
                token);
    }
}
