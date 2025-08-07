package com.daon.be.child.service;

import com.daon.be.child.dto.ChildInterestCreateRequestDTO;
import com.daon.be.child.dto.ChildInterestDeleteRequestDTO;
import com.daon.be.child.dto.ChildRequestDTO;
import com.daon.be.child.dto.ChildResponseDTO;
import com.daon.be.child.dto.ChildProfileResponseDTO;

import java.util.List;

public interface ChildService {

    // 1. 자녀 등록
    ChildResponseDTO createChild(Long userId, ChildRequestDTO requestDTO);

    // 2. 자녀 전체 조회
    List<ChildProfileResponseDTO> getAllChildren(Long userId);

    // 3. 자녀 단건 조회
    ChildProfileResponseDTO getChildProfile(Long childId);

    // 4. 자녀 프로필 수정
    void updateChildProfile(Long childId, String name, String birthDate, String profileImg);

    // 5. 자녀 삭제
    void deleteChild(Long childId);

    // 6. 관심사 추가
    void addChildInterests(Long childId, ChildInterestCreateRequestDTO dto);

    // 7. 관심사 삭제
    void deleteChildInterests(Long childId, ChildInterestDeleteRequestDTO dto);
}
