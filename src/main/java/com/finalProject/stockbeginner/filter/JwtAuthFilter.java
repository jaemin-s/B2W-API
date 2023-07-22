package com.finalProject.stockbeginner.filter;


import com.finalProject.stockbeginner.user.auth.TokenProvider;
import com.finalProject.stockbeginner.user.auth.TokenUserInfo;
import com.finalProject.stockbeginner.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.GenericFilter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseBearerToken(request);
            log.info("Jwt Token Filter is running... - token: {}", token);
            if (token != null) {
                TokenUserInfo userInfo
                        = tokenProvider.validateAndGetTokenUserInfo(token);
                List<SimpleGrantedAuthority> authorityList
                        = new ArrayList<>();
                authorityList.add(new SimpleGrantedAuthority("User"));
                AbstractAuthenticationToken auth
                        = new UsernamePasswordAuthenticationToken(
                        userInfo, // 컨트롤러에서 활용할 유저 정보
                        null, // 인증된 사용자의 비밀번호 - 보통 널값
                        authorityList // 인가 정보 (권한 정보)
                );

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);

            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("토큰이 위조 되었거나 토큰이 만료되었습니다.");
        }
        filterChain.doFilter(request, response);
    }

    private String parseBearerToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

