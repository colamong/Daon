package com.daon.be.child.dto;

import com.daon.be.child.entity.ChildProfile.Gender;
import lombok.*;

import java.util.List;
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
    /** 서버에 저장된 S3 key 또는 (과거에 저장한) 공개 URL */
    private String profileImg;

    /** 프리사인 URL: 프런트에서 <img :src="imageUrl"> 로 바로 사용 */
    private String imageUrl;

    private List<String> registeredInterests;
}
