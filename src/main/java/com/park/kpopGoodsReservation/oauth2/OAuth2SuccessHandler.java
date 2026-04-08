package com.park.kpopGoodsReservation.oauth2;

import com.park.kpopGoodsReservation.entity.Member;
import com.park.kpopGoodsReservation.jwt.JwtProvider;
import com.park.kpopGoodsReservation.repository.MemberRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // 1. 구글에서 받아온 유저 정보 꺼내기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String nickname = (String) attributes.get("name");
        String provider = "google";

        // 2. DB에 없으면 저장 (최초 가입)
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(
                        Member.create(email, nickname, provider)
                ));

        // 3. JWT 발급
        String token = jwtProvider.generateToken(member.getEmail());

        // 4. 쿠키에 토큰 담기
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);  // JS에서 접근 못하게
        cookie.setPath("/");       // 모든 경로에서 사용 가능
        cookie.setMaxAge(60 * 60 * 24); // 1일
        response.addCookie(cookie);

        // 5. 메인 페이지로 이동
        response.sendRedirect("/main");
    }
}