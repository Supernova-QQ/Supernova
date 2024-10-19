package com.hanshin.supernova.security.jwt;

import com.hanshin.supernova.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.netty.handler.codec.http.HttpHeaderNames;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static com.hanshin.supernova.security.TokenConstants.USER_AUTHORITY_CLAIM;
import static com.hanshin.supernova.security.TokenConstants.USER_EMAIL_CLAIM;

@Component
@RequiredArgsConstructor
public class JwtFilterImpl extends JwtFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Enumeration<String> authorizationHeaders = request.getHeaders(HttpHeaderNames.AUTHORIZATION.toString());
        Authentication authentication = null;
        boolean shortCircuit = false;

        while (authorizationHeaders.hasMoreElements()) {
            String header = authorizationHeaders.nextElement();

            if (header == null || !header.startsWith("Bearer ")) {
                continue;
            }

            String accessToken = header.substring("Bearer ".length());
            if (accessToken.isBlank()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access token required");
                shortCircuit = true;
                break;
            }

            Claims claims = jwtService.validateAccessToken(accessToken);
            if (claims == null) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid access token");
                shortCircuit = true;
            } else {
                String email = claims.get(USER_EMAIL_CLAIM, String.class);
                String role = claims.get(USER_AUTHORITY_CLAIM, String.class);
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));

                authentication = UsernamePasswordAuthenticationToken.authenticated(email, null, authorities);
            }
            break;
        }

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        if (!shortCircuit) {
            filterChain.doFilter(request, response);
        }
    }
}