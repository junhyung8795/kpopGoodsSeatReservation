package com.park.kpopGoodsReservation.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    // private final MemberRepository memberRepository; ← 제거
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 쿠키에서 토큰 꺼내기
        String token = extractToken(request);
        log.info("토큰 존재 여부: {}", token != null);

        // 2. 토큰 있고 유효하면 인증 처리
        if (token != null && jwtProvider.validateToken(token)) {

            // 3. 토큰에서 이메일 꺼내기
            String email = jwtProvider.getEmail(token);

            // 4. DB에서 유저 조회
            String role = jwtProvider.getRole(token);
            log.info("인증된 유저: {}, 권한: {}", email, role); // ← 여기 찍히면 필터 동작 ✅


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email, // Member 객체 대신 이메일만
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            }


        // 7. 다음 필터로 이동
        filterChain.doFilter(request, response);
    }

    // 쿠키에서 토큰 추출
    private String extractToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals("token")) {
                return cookie.getValue();
            }
        }
        return null;
    }
}