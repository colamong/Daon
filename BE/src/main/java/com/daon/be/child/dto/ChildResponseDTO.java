package com.daon.be.child.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildResponseDTO {
    private Long childId;
    private String name;
    private List<String> registeredInterests;
}
