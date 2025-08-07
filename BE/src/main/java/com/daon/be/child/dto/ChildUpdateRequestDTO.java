package com.daon.be.child.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildUpdateRequestDTO {
    private String name;
    private LocalDate birthDate;
    private String profileImg;
}
