package com.example.dosirakbe.domain.chat_room.controller;

import com.example.dosirakbe.domain.auth.dto.response.CustomOAuth2User;
import com.example.dosirakbe.domain.chat_room.dto.request.ChatRoomRegisterRequest;
import com.example.dosirakbe.domain.chat_room.dto.response.ChatRoomBriefResponse;
import com.example.dosirakbe.domain.chat_room.dto.response.ChatRoomByUserResponse;
import com.example.dosirakbe.domain.chat_room.dto.response.ChatRoomInformationResponse;
import com.example.dosirakbe.domain.chat_room.dto.response.ChatRoomResponse;
import com.example.dosirakbe.domain.chat_room.service.ChatRoomService;
import com.example.dosirakbe.domain.user.dto.response.UserChatRoomResponse;
import com.example.dosirakbe.global.util.ApiResult;
import com.example.dosirakbe.global.util.StatusEnum;
import com.example.dosirakbe.global.util.ValidationUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ApiResult<ChatRoomResponse>> createChatRoom(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                      @Valid @RequestBody ChatRoomRegisterRequest createRequest,
                                                                      BindingResult bindingResult) {
        ValidationUtils.validationRequest(bindingResult);

        Long userId = getUserIdByOAuth(customOAuth2User);
        ChatRoomResponse chatRoomResponse = chatRoomService.createChatRoom(createRequest, userId);

        ApiResult<ChatRoomResponse> result = ApiResult.<ChatRoomResponse>builder()
                .status(StatusEnum.SUCCESS)
                .message("Chat room created successfully")
                .data(chatRoomResponse)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(result);
    }

    @GetMapping("/{id}/information")
    public ResponseEntity<ApiResult<ChatRoomInformationResponse>> getChatRoomInformation(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                                         @PathVariable Long id) {
        Long userId = getUserIdByOAuth(customOAuth2User);
        ChatRoomInformationResponse chatRoomInformation = chatRoomService.findMessagesByChatRoom(userId, id);

        ApiResult<ChatRoomInformationResponse> result = ApiResult.<ChatRoomInformationResponse>builder()
                .status(StatusEnum.SUCCESS)
                .message("Chat room information retrieved successfully")
                .data(chatRoomInformation)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/{id}/user-list")
    public ResponseEntity<ApiResult<List<UserChatRoomResponse>>> getChatRoomUsers(@PathVariable Long id) {
        List<UserChatRoomResponse> userList = chatRoomService.findUserChatRooms(id);

        ApiResult<List<UserChatRoomResponse>> result = ApiResult.<List<UserChatRoomResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("User list retrieved successfully")
                .data(userList)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResult<Void>> deleteChatRoom(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                          @PathVariable Long id) {
        Long userId = getUserIdByOAuth(customOAuth2User);
        chatRoomService.leaveChatRoom(userId, id);

        ApiResult<Void> result = ApiResult.<Void>builder()
                .status(StatusEnum.SUCCESS)
                .message("Chat room deleted successfully")
                .build();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(result);
    }

    @GetMapping("/zone-category/{zoneCategoryName}")
    public ResponseEntity<ApiResult<List<ChatRoomBriefResponse>>> chatRoomByZoneCategory(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                                                                                         @PathVariable String zoneCategoryName) {
        Long userId = getUserIdByOAuth(customOAuth2User);
        List<ChatRoomBriefResponse> chatRoomByZoneCategory = chatRoomService.findChatRoomByZoneCategory(userId, zoneCategoryName);

        ApiResult<List<ChatRoomBriefResponse>> result = ApiResult.<List<ChatRoomBriefResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("Chat rooms by zone category retrieved successfully")
                .data(chatRoomByZoneCategory)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping
    public ResponseEntity<ApiResult<List<ChatRoomBriefResponse>>> getAllChatRooms(@RequestParam(required = false, defaultValue = "recent") String sort,
                                                                                  @RequestParam(required = false) String search) {
        List<ChatRoomBriefResponse> allChatRoomBySearchAndSort = chatRoomService.findAllChatRoomBySearchAndSort(sort, search);

        ApiResult<List<ChatRoomBriefResponse>> result = ApiResult.<List<ChatRoomBriefResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("All chat rooms retrieved successfully")
                .data(allChatRoomBySearchAndSort)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    @GetMapping("/by-user")
    public ResponseEntity<ApiResult<List<ChatRoomByUserResponse>>> myChatRoomList(@AuthenticationPrincipal CustomOAuth2User customOAuth2User) {
        Long userId = getUserIdByOAuth(customOAuth2User);
        List<ChatRoomByUserResponse> allByUser = chatRoomService.findAllByUser(userId);

        ApiResult<List<ChatRoomByUserResponse>> result = ApiResult.<List<ChatRoomByUserResponse>>builder()
                .status(StatusEnum.SUCCESS)
                .message("Chat rooms by user retrieved successfully")
                .data(allByUser)
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(result);
    }

    private Long getUserIdByOAuth(CustomOAuth2User customOAuth2User) {
        return customOAuth2User.getUserDTO().getUserId();
    }
}