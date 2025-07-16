package com.ssafy.test.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.test.dto.UserDto;

import java.util.Map;


@RestController
public class UserController {

    @GetMapping
    public String home() {
        return "Welcome Home!";
    }

    @GetMapping("/mypage")
    public UserDto mypage(@AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser == null) {
            return null;
        }

        // 카카오 응답 구조에서 꺼내기
        String email = null;
        String nickname = null;
        String picture = null;

        if (oidcUser.getAttributes().containsKey("kakao_account")) {
            var account = (Map<String, Object>) oidcUser.getAttributes().get("kakao_account");
            email = (String) account.get("email");

            if (account.containsKey("profile")) {
                var profile = (Map<String, Object>) account.get("profile");
                nickname = (String) profile.get("nickname");
                picture = (String) profile.get("profile_image_url");
            }
        } else {
            // 구글 등 OIDC 표준
            email = oidcUser.getEmail();
            nickname = oidcUser.getFullName();
            picture = oidcUser.getPicture();
        }

        return new UserDto(email, nickname, picture);
    }


    @GetMapping("/error")
    public String error() {
        return "Login failed!";
    }
}
