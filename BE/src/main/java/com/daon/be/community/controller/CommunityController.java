package com.daon.be.community.controller;

import com.daon.be.community.dto.request.CommunityJoinRequestDto;
import com.daon.be.community.dto.request.CommunityLeaveRequestDto;
import com.daon.be.community.dto.request.ChatMessageRequestDto;
import com.daon.be.community.dto.response.ChatMessageResponseDto;
import com.daon.be.community.dto.response.CommunityListResponseDto;
import com.daon.be.community.dto.response.CommunityResponseDto;
import com.daon.be.community.dto.response.ParticipantResponseDto;
import com.daon.be.community.service.ChatMessageService;
import com.daon.be.community.service.CommunityService;
import com.daon.be.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;   // ★ 추가
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;   // ★ 추가

    @GetMapping
    public ResponseEntity<ApiResponse<CommunityListResponseDto>> getAllCommunities() {
        CommunityListResponseDto response = communityService.getAllCommunities();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<CommunityListResponseDto>> searchCommunities(
            @RequestParam String title) {
        CommunityListResponseDto response = communityService.searchCommunitiesByTitle(title);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<CommunityListResponseDto>> getActiveCommunities(
            @RequestParam Long userId) {
        CommunityListResponseDto response = communityService.getActiveCommunitiesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{communityId}/join")
    public ResponseEntity<ApiResponse<CommunityResponseDto>> joinCommunity(
            @PathVariable Long communityId,
            @RequestBody CommunityJoinRequestDto requestDto) {
        communityService.joinCommunity(communityId, requestDto.getUserId());
        CommunityResponseDto community = communityService.getCommunityById(communityId);
        return ResponseEntity.ok(ApiResponse.success(community));
    }

    @DeleteMapping("/{communityId}/leave")
    public ResponseEntity<ApiResponse<Void>> leaveCommunity(
            @PathVariable Long communityId,
            @RequestBody CommunityLeaveRequestDto requestDto) {
        communityService.leaveCommunity(communityId, requestDto.getUserId());
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{communityId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDto>>> getMessages(
            @PathVariable Long communityId,
            @RequestParam Long userId) {
        List<ChatMessageResponseDto> response = chatMessageService.getMessagesByCommunityIdForUser(communityId, userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/{communityId}/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponseDto>> sendMessage(
            @PathVariable Long communityId,
            @RequestBody ChatMessageRequestDto requestDto) {

        // 1) 저장
        ChatMessageResponseDto saved = chatMessageService.sendMessage(communityId, requestDto);

        // 2) 실시간 브로드캐스트 ★ 핵심 한 줄
        messagingTemplate.convertAndSend("/topic/community/" + communityId, saved);

        // 3) 응답
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    @GetMapping("/{communityId}/participants")
    public ResponseEntity<ApiResponse<List<ParticipantResponseDto>>> getParticipants(
            @PathVariable Long communityId) {
        List<ParticipantResponseDto> response = communityService.getParticipants(communityId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
