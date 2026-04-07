package com.park.kpopGoodsReservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String provider; // google, kakao, naver

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 최초 가입 시 생성
    public static Member create(String email, String nickname, String provider) {
        Member member = new Member();
        member.email = email;
        member.nickname = nickname;
        member.provider = provider;
        member.role = Role.USER;
        return member;
    }
}