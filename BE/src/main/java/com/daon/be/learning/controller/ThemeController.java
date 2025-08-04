package com.daon.be.learning.controller;

import com.daon.be.learning.dto.response.ThemeResponseDTO;
import com.daon.be.learning.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/study/themes")
public class ThemeController {

    private final ThemeService themeService;

    @GetMapping
    public ResponseEntity<List<ThemeResponseDTO>> getAllThemes(){
        List<ThemeResponseDTO> themes = themeService.getAllThemes();
        return ResponseEntity.ok(themes);
    }
}
