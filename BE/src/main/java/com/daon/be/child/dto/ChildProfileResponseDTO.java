package com.daon.be.child.dto;

import com.daon.be.child.entity.ChildProfile.Gender;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildProfileResponseDTO {
    private Long childId;
    private String name;
    private LocalDate birthDate;
    private Gender gender;
    private String profileImg;
}
