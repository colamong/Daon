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
import com.daon.be.global.infra.S3Uploader;
import com.daon.be.user.entity.User;
import com.daon.be.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;

import java.io.IOException;
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

    // S3 연동
    private final S3Uploader s3Uploader; // upload() presignGetUrl() 사용
    private final S3Client s3Client;     // deleteObject() 사용

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Override
    public ChildResponseDTO createChild(Long userId, ChildRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        ChildProfile child = ChildProfile.builder()
                .user(user)
                .name(requestDTO.getName())
                .birthDate(requestDTO.getBirthDate())
                .gender(requestDTO.getGender())
                .profileImg(requestDTO.getProfileImg()) // 현재 구조 유지
                .build();

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
                            .map(ChildInterest::getName)
                            .collect(Collectors.toList());

                    String imageUrl = toImageUrl(child.getProfileImg()); // 프리사인 또는 기존 URL

                    return ChildProfileResponseDTO.builder()
                            .childId(child.getId())
                            .name(child.getName())
                            .birthDate(child.getBirthDate())
                            .gender(child.getGender())
                            .profileImg(child.getProfileImg()) // 저장값 key 또는 과거 URL
                            .imageUrl(imageUrl)                 // 바로 <img src>
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

        String imageUrl = toImageUrl(child.getProfileImg());

        return ChildProfileResponseDTO.builder()
                .childId(child.getId())
                .name(child.getName())
                .birthDate(child.getBirthDate())
                .gender(child.getGender())
                .profileImg(child.getProfileImg())
                .imageUrl(imageUrl)
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
        child.setProfileImg(profileImg); // 이미지 교체는 전용 엔드포인트 사용
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


    // 이미지 업로드 교체
    @Override
    public void updateChildImage(Long userId, Long childId, MultipartFile file) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new EntityNotFoundException("자녀 정보 없음"));

        if (!child.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        // 기존 이미지 삭제 key 또는 URL 저장 가능성 모두 처리
        String old = child.getProfileImg();
        if (old != null && !old.isBlank()) {
            try {
                String keyToDelete = extractKey(old);
                if (keyToDelete != null) {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(keyToDelete)
                            .build());
                }
            } catch (Exception ignore) { /* 실패해도 신규 업로드 진행 */ }
        }

        // 새 업로드
        String ext = extOf(file.getOriginalFilename());
        String key = "child/%d/%s.%s".formatted(childId, java.util.UUID.randomUUID(), ext);
        try {
            String saved = s3Uploader.upload(key, file.getBytes(), file.getContentType());
            // 권장 저장 형태 key
            child.setProfileImg(saved);
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }

    // 파일 확장자
    private static String extOf(String name) {
        int i = name == null ? -1 : name.lastIndexOf('.');
        return i > -1 ? name.substring(i + 1).toLowerCase() : "jpg";
    }

    // URL 또는 key에서 key 추출 가상 호스팅 패스 스타일 모두 대응
    private String extractKey(String stored) {
        if (stored == null || stored.isBlank()) return null;
        if (!stored.startsWith("http")) return stored;

        try {
            java.net.URI uri = java.net.URI.create(stored);
            String host = uri.getHost();
            String path = uri.getPath();
            if (host == null || path == null) return stored;

            // 가상 호스팅 https://bucket.s3.region.amazonaws.com/key
            if (host.contains(".s3.")) {
                String p = path.startsWith("/") ? path.substring(1) : path;
                if (!host.startsWith("s3.")) {
                    return p; // key
                }
            }

            // 패스 스타일 https://s3.region.amazonaws.com/bucket/key
            if (host.startsWith("s3.")) {
                String p = path.startsWith("/") ? path.substring(1) : path; // bucket/key
                int slash = p.indexOf('/');
                if (slash > 0 && slash < p.length() - 1) {
                    return p.substring(slash + 1); // key
                }
            }

            int last = path.lastIndexOf('/');
            return last >= 0 && last < path.length() - 1 ? path.substring(last + 1) : path;
        } catch (Exception e) {
            return stored;
        }
    }

    // 저장값이 key면 프리사인 URL을 생성 과거에 URL 저장한 경우 그대로 반환
    private String toImageUrl(String stored) {
        if (stored == null || stored.isBlank()) return null;
        if (stored.startsWith("http")) return stored;
        return s3Uploader.presignGetUrl(stored);
    }
}
