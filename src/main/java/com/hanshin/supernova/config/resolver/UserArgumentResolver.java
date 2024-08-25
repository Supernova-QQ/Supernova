package com.hanshin.supernova.config.resolver;

import com.hanshin.supernova.auth.application.TokenService;
import com.hanshin.supernova.auth.model.AuthToken;
import com.hanshin.supernova.auth.model.AuthUser;
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

    private final TokenService tokenService;

    @Override
    public boolean supportsParameter(
            MethodParameter parameter) {
        return parameter.getParameterType()
                .equals(AuthUser.class);
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

        var token = new AuthToken(accessToken);

        return tokenService.getAuthUser(
                token);
    }
}
