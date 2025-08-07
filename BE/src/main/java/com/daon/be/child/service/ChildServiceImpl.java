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

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChildServiceImpl implements ChildService {

    private final UserRepository userRepository;
    private final ChildProfileRepository childProfileRepository;
    private final ChildInterestRepository childInterestRepository;

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
        // 부모를 즉시 DB에 반영
        ChildProfile savedChild = childProfileRepository.saveAndFlush(child);

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

    @Override
    public List<ChildProfileResponseDTO> getAllChildren(Long userId) {
        // userId 검증
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

    @Override
    public ChildProfileResponseDTO getChildProfile(Long userId, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        // 소유권 검증
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        return ChildProfileResponseDTO.builder()
                .childId(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .profileImg(child.getProfileImg())
                .build();
    }

    @Override
    public void updateChildProfile(Long userId, Long childId,
                                   String name, String birthDate, String profileImg) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        child.setName(name);
        child.setBirthDate(LocalDate.parse(birthDate));
        child.setProfileImg(profileImg);
    }

    @Override
    public void deleteChild(Long userId, Long childId) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        childProfileRepository.delete(child);
    }

    @Override
    public void addChildInterests(Long userId, Long childId, ChildInterestCreateRequestDTO dto) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        List<ChildInterest> interests = dto.getInterests().stream()
                .map(name -> ChildInterest.builder()
                        .childProfile(child)
                        .name(name)
                        .build())
                .collect(Collectors.toList());
        childInterestRepository.saveAll(interests);
    }

    @Override
    public void deleteChildInterests(Long userId, Long childId, ChildInterestDeleteRequestDTO dto) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }
        childInterestRepository.deleteByChildProfileIdAndNameIn(childId, dto.getInterests());
    }
}
