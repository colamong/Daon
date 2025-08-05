package com.daon.be.learning.service;

import com.daon.be.learning.dto.response.ThemeResponseDTO;
import com.daon.be.learning.entity.Theme;
import com.daon.be.learning.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    public List<ThemeResponseDTO> getAllThemes(){
        List<Theme> themes = themeRepository.findAll();

        return themes.stream()
                .map(theme -> ThemeResponseDTO.builder()
                        .id(theme.getId())
                        .name(theme.getName())
                        .description(theme.getDescription())
                        .build())
                .collect(Collectors.toList());

    }


}
