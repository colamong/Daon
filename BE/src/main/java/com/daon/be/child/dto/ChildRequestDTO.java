package com.daon.be.child.dto;

import com.daon.be.child.entity.ChildProfile.Gender;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildRequestDTO {
    private Long userId; // 사용자의 ID
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private String profileImg;
    private List<String> interests; // 관심사 문자열 리스트
}
