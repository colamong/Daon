package com.daon.be.child.controller;

import com.daon.be.child.dto.ChildInterestCreateRequestDTO;
import com.daon.be.child.dto.ChildInterestDeleteRequestDTO;
import com.daon.be.child.dto.ChildProfileResponseDTO;
import com.daon.be.child.dto.ChildRequestDTO;
import com.daon.be.child.dto.ChildResponseDTO;
import com.daon.be.child.dto.ChildUpdateRequestDTO;
import com.daon.be.child.service.ChildService;
import com.daon.be.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    /** 1. 자녀 프로필 등록 */
    @PostMapping
    public ResponseEntity<ApiResponse<ChildResponseDTO>> createChild(
            @PathVariable Long userId,
            @RequestBody ChildRequestDTO dto
    ) {
        ChildResponseDTO data = childService.createChild(userId, dto);
        return new ResponseEntity<>(
                new ApiResponse<>(201, "자녀 프로필 등록 완료", data),
                HttpStatus.CREATED
        );
    }

    /** 2. 자녀 전체 조회 */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChildProfileResponseDTO>>> getAllChildren(
            @PathVariable Long userId
    ) {
        List<ChildProfileResponseDTO> data = childService.getAllChildren(userId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "자녀 목록 조회 성공", data)
        );
    }

    /** 3. 자녀 단건 조회 */
    @GetMapping("/{childId}")
    public ResponseEntity<ApiResponse<ChildProfileResponseDTO>> getChildProfile(
            @PathVariable Long userId,
            @PathVariable Long childId
    ) {
        ChildProfileResponseDTO data = childService.getChildProfile(userId, childId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "자녀 프로필 조회 성공", data)
        );
    }

    /** 4. 자녀 프로필 수정 */
    @PutMapping("/{childId}")
    public ResponseEntity<ApiResponse<Void>> updateChild(
            @PathVariable Long userId,
            @PathVariable Long childId,
            @RequestBody ChildUpdateRequestDTO dto
    ) {
        childService.updateChildProfile(
                userId,
                childId,
                dto.getName(),
                dto.getBirthDate().toString(),
                dto.getProfileImg()
        );
        return ResponseEntity.ok(
                new ApiResponse<>(200, "자녀 프로필 수정 완료", null)
        );
    }

    /** 5. 자녀 프로필 삭제 */
    @DeleteMapping("/{childId}")
    public ResponseEntity<ApiResponse<Void>> deleteChild(
            @PathVariable Long userId,
            @PathVariable Long childId
    ) {
        childService.deleteChild(userId, childId);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "자녀 프로필 삭제 완료", null)
        );
    }

    /** 6. 자녀 관심사 추가 */
    @PostMapping("/{childId}/interest")
    public ResponseEntity<ApiResponse<Void>> addInterests(
            @PathVariable Long userId,
            @PathVariable Long childId,
            @RequestBody ChildInterestCreateRequestDTO dto
    ) {
        childService.addChildInterests(userId, childId, dto);
        return new ResponseEntity<>(
                new ApiResponse<>(201, "자녀 관심사 등록 완료", null),
                HttpStatus.CREATED
        );
    }

    /** 7. 자녀 관심사 삭제 */
    @DeleteMapping("/{childId}/interest")
    public ResponseEntity<ApiResponse<Void>> deleteInterests(
            @PathVariable Long userId,
            @PathVariable Long childId,
            @RequestBody ChildInterestDeleteRequestDTO dto
    ) {
        childService.deleteChildInterests(userId, childId, dto);
        return ResponseEntity.ok(
                new ApiResponse<>(200, "자녀 관심사 삭제 완료", null)
        );
    }
}
