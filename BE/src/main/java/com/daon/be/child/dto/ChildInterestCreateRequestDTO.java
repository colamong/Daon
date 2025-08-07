package com.daon.be.child.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildInterestCreateRequestDTO {
    private List<String> interests;
}
