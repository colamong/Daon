package com.ssafy.test.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @GetMapping
    public String home() {
        return "Welcome Home!";
    }

    @GetMapping("/mypage")
    public Map<String, Object> mypage(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return Map.of("error", "User not logged in");
        }
        return oidcUser.getClaims();
    }

    @GetMapping("/error")
    public String error() {
        return "Login failed!";
    }
}
