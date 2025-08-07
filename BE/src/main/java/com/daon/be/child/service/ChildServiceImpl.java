package com.daon.be.child.service;

import com.daon.be.child.dto.ChildInterestCreateRequestDTO;
import com.daon.be.child.dto.ChildInterestDeleteRequestDTO;
import com.daon.be.child.dto.ChildProfileResponseDTO;
import com.daon.be.child.dto.ChildRequestDTO;
import com.daon.be.child.dto.ChildResponseDTO;
import com.daon.be.child.entity.ChildInterest;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.repository.ChildInterestRepository;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildServiceImpl implements ChildService {

    private final UserRepository userRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildInterestRepository childInterestRepository;

    // 1. 자녀 등록
    @Override
    public ChildResponseDTO createChild(Long userId, ChildRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChildProfile child = ChildProfile.builder()
                .user(user)
                .name(requestDTO.getName())
                .birthDate(requestDTO.getBirthDate())
                .gender(requestDTO.getGender())
                .profileImg(requestDTO.getProfileImg())
                .build();
        ChildProfile savedChild = childProfileRepository.save(child);

        List<ChildInterest> interests = requestDTO.getInterests().stream()
                .map(name -> ChildInterest.builder()
                        .childProfile(savedChild)
                        .name(name)
                        .build())
                .collect(Collectors.toList());
        childInterestRepository.saveAll(interests);

        return ChildResponseDTO.builder()
                .childId(savedChild.getId())
                .name(savedChild.getName())
                .registeredInterests(
                        interests.stream()
                                .map(ChildInterest::getName)
                                .collect(Collectors.toList())
                )
                .build();
    }

    // 2. 자녀 전체 조회
    @Override
    public List<ChildProfileResponseDTO> getAllChildren(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
        return childProfileRepository.findByUserId(userId).stream()
                .map(child -> ChildProfileResponseDTO.builder()
                        .childId(child.getId())
                        .name(child.getName())
                        .birthDate(child.getBirthDate())
                        .gender(child.getGender())
                        .profileImg(child.getProfileImg())
                        .build())
                .collect(Collectors.toList());
    }

    // 3. 자녀 단건 조회
    @Override
    public ChildProfileResponseDTO getChildProfile(Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        return ChildProfileResponseDTO.builder()
                .childId(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .profileImg(child.getProfileImg())
                .build();
    }

    // 4. 자녀 프로필 수정
    @Override
    public void updateChildProfile(Long childId, String name, String birthDate, String profileImg) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        child.setName(name);
        child.setBirthDate(java.time.LocalDate.parse(birthDate));
        child.setProfileImg(profileImg);
    }

    // 5. 자녀 삭제
    @Override
    public void deleteChild(Long childId) {
        childInterestRepository.deleteById(childId);
        childProfileRepository.deleteById(childId);
    }

    // 6. 관심사 추가
    @Override
    public void addChildInterests(Long childId, ChildInterestCreateRequestDTO dto) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        List<ChildInterest> interests = dto.getInterests().stream()
                .map(name -> ChildInterest.builder()
                        .childProfile(child)
                        .name(name)
                        .build())
                .collect(Collectors.toList());
        childInterestRepository.saveAll(interests);
    }

    // 7. 관심사 삭제
    @Override
    public void deleteChildInterests(Long childId, ChildInterestDeleteRequestDTO dto) {
        childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        childInterestRepository.deleteByChildProfileIdAndNameIn(childId, dto.getInterests());
    }
}
