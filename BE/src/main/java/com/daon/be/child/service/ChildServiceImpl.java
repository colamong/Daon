package com.daon.be.child.service;

import com.daon.be.child.dto.ChildInterestCreateRequestDTO;
import com.daon.be.child.dto.ChildInterestDeleteRequestDTO;
import com.daon.be.child.dto.ChildProfileResponseDTO;
import com.daon.be.child.dto.ChildRequestDTO;
import com.daon.be.child.dto.ChildResponseDTO;
import com.daon.be.child.entity.ChildInterest;
import com.daon.be.child.entity.ChildProfile;
import com.daon.be.child.entity.InterestAuthor;
import com.daon.be.child.repository.ChildInterestRepository;
import com.daon.be.child.repository.ChildProfileRepository;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
                        .interestType(name)
                        .build())
                .collect(Collectors.toList());
        childInterestRepository.saveAll(interests);

        return ChildResponseDTO.builder()
                .childId(savedChild.getId())
                .name(savedChild.getName())
                .registeredInterests(
                        interests.stream()
                                .map(ChildInterest::getInterestType)
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
                .map(child ->  {
                    // 각 child에 대해 관심사 조회
                    List<String> interests = childInterestRepository
                            .findByChildProfileId(child.getId())
                            .stream()
                            .map(ChildInterest::getInterestType)
                            .collect(Collectors.toList());

                    return ChildProfileResponseDTO.builder()
                            .childId(child.getId())
                            .name(child.getName())
                            .birthDate(child.getBirthDate())
                            .gender(child.getGender())
                            .profileImg(child.getProfileImg())
                            .registeredInterests(interests)
                            .build();
                })
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
        System.out.println("=== addChildInterests 호출 ===");
        System.out.println("userId: " + userId + ", childId: " + childId);
        System.out.println("요청된 관심사: " + dto.getInterests());

        ChildProfile child = childProfileRepository.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 기존 name 집합(정규화 키)
        List<ChildInterest> existing = childInterestRepository.findByChildProfileId(childId);
        Set<String> existingKeys = existing.stream()
            .map(ChildInterest::getName)
            .filter(Objects::nonNull)
            .map(this::normKey)
            .collect(Collectors.toSet());

        // 입력: 정규화 + 중복 제거 -> 새 항목만 생성
        LinkedHashSet<String> requested = dto.getInterests().stream()
            .map(this::normalize)
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        List<ChildInterest> toCreate = requested.stream()
            .filter(n -> !existingKeys.contains(normKey(n)))
            .map(n -> ChildInterest.of(child, n, InterestAuthor.PARENT)) // 팩토리 사용
            .toList();

        System.out.println("추가할 새로운 관심사: " + toCreate.size() + "개");
        toCreate.forEach(ci -> System.out.println("- " + ci.getName()));

        if (!toCreate.isEmpty()) {
            childInterestRepository.saveAll(toCreate);
            System.out.println("DB 저장 완료");
        } else {
            System.out.println("추가할 새로운 관심사 없음 (모두 중복)");
        }

        System.out.println("=== addChildInterests 완료 ===");
    }

    @Override
    public void deleteChildInterests(Long userId, Long childId, ChildInterestDeleteRequestDTO dto) {
        ChildProfile child = childProfileRepository.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // 삭제 요청 집합(정규화 키)
        Set<String> targets = dto.getInterests().stream()
            .map(this::normalize)
            .filter(Objects::nonNull)
            .map(this::normKey)
            .collect(Collectors.toSet());

        if (targets.isEmpty()) return;

        // 실제 DB 값 중에서 삭제 대상 이름 원본 리스트 추출(대소문자/공백 차이 흡수)
        List<String> actualNamesToDelete = childInterestRepository.findByChildProfileId(childId).stream()
            .map(ChildInterest::getName)
            .filter(Objects::nonNull)
            .filter(name -> targets.contains(normKey(name)))
            .toList();

        if (!actualNamesToDelete.isEmpty()) {
            // 레포지토리: deleteByChildProfileIdAndNameIn 사용
            childInterestRepository.deleteByChildProfileIdAndNameIn(childId, actualNamesToDelete);
        }
    }

    /* 유틸 */
    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        return t.length() > 100 ? t.substring(0, 100) : t;
    }

    private String normKey(String s) {
        return s.trim().toLowerCase();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getParentInterests(Long userId, Long childId) {
        return getInterestsByAuthor(userId, childId, InterestAuthor.PARENT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAiInterests(Long userId, Long childId) {
        return getInterestsByAuthor(userId, childId, InterestAuthor.AI);
    }

    private List<String> getInterestsByAuthor(Long userId, Long childId, InterestAuthor author) {
        ChildProfile child = childProfileRepository.findById(childId)
            .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));

        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        // name 우선, 없으면 interest_type 폴백
        Function<ChildInterest, String> label = ci -> {
            String v = ci.getName();
            if (v == null || v.isBlank()) v = ci.getInterestType();
            return v == null ? "" : v.trim();
        };

        return childInterestRepository.findByChildProfileIdAndAuthor(childId, author).stream()
            .map(label)
            .filter(s -> s != null) // 비어있는 값은 제외 (필요하면 제거)
            .distinct()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
    }

}
