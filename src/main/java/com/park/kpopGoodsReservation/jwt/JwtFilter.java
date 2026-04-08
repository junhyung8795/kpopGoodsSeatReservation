package com.park.kpopGoodsReservation.jwt;

import com.park.kpopGoodsReservation.entity.Member;
import com.park.kpopGoodsReservation.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 쿠키에서 토큰 꺼내기
        String token = extractToken(request);

        // 2. 토큰 있고 유효하면 인증 처리
        if (token != null && jwtProvider.validateToken(token)) {

            // 3. 토큰에서 이메일 꺼내기
            String email = jwtProvider.getEmail(token);

            // 4. DB에서 유저 조회
            Member member = memberRepository.findByEmail(email)
                    .orElse(null);

            if (member != null) {
                // 5. 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                member,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole().name()))
                        );

                // 6. SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
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