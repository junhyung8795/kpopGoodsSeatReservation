package com.park.kpopGoodsReservation.jwt;

import com.park.kpopGoodsReservation.entity.Member;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key = Jwts.SIG.HS256.key().build();
    private final long EXPIRATION = 1000 * 60 * 60 * 24; // 1일

    // JWT 생성 (Member 객체 받도록 변경)
    public String generateToken(Member member) {
        return Jwts.builder()
                .subject(member.getEmail())
                .claim("role", member.getRole().name()) // role 담기
                .claim("nickname", member.getNickname()) // nickname 담기
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    // 이메일 꺼내기
    public String getEmail(String token) {
        return getClaims(token).getSubject();
    }

    // role 꺼내기
    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // 공통 claims 꺼내기
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // JWT 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}