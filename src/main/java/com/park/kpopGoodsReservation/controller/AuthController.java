package com.park.kpopGoodsReservation.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        // SecurityContext에 인증 정보 있으면 이미 로그인된 것
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/main";
        }
        return "login";
    }

    @GetMapping("/main")
    public String mainPage() {
        return "main"; // templates/main.html
    }
}