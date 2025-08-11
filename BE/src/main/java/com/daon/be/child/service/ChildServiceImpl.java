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
import java.util.List;
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
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        return childProfileRepository.findByUserId(userId).stream()
                .map(child ->  {
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
    public void addChildInterests(Long userId, Long childId,
                                  ChildInterestCreateRequestDTO dto) {
        ChildProfile child = childProfileRepository.findById(childId)
                .orElseThrow(() -> new IllegalArgumentException("자녀 정보 없음"));
        if (!child.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        List<String> existingInterests = childInterestRepository
                .findByChildProfileId(childId)
                .stream()
                .map(ChildInterest::getName)
                .collect(Collectors.toList());

        List<ChildInterest> newInterests = dto.getInterests().stream()
                .filter(name -> !existingInterests.contains(name))
                .map(name -> ChildInterest.builder()
                        .childProfile(child)
                        .name(name)
                        .build())
                .collect(Collectors.toList());

        if (!newInterests.isEmpty()) {
            childInterestRepository.saveAll(newInterests);
        }
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
