package com.daon.be.child.service;

import com.daon.be.child.dto.ChildInterestCreateRequestDTO;
import com.daon.be.child.dto.ChildInterestDeleteRequestDTO;
import com.daon.be.child.dto.ChildProfileResponseDTO;
import com.daon.be.child.dto.ChildRequestDTO;
import com.daon.be.child.dto.ChildResponseDTO;

import java.util.List;

public interface ChildService {

    ChildResponseDTO createChild(Long userId, ChildRequestDTO requestDTO);

    List<ChildProfileResponseDTO> getAllChildren(Long userId);

    ChildProfileResponseDTO getChildProfile(Long userId, Long childId);

    void updateChildProfile(Long userId, Long childId,
                            String name, String birthDate, String profileImg);

    void deleteChild(Long userId, Long childId);

    void addChildInterests(Long userId, Long childId, ChildInterestCreateRequestDTO dto);

    void deleteChildInterests(Long userId, Long childId, ChildInterestDeleteRequestDTO dto);
}
