package com.example.dosirakbe.domain.chat_room.controller;

import com.example.dosirakbe.domain.chat_room.dto.request.ChatRoomRegisterRequest;
import com.example.dosirakbe.domain.chat_room.dto.response.ChatRoomResponse;
import com.example.dosirakbe.domain.chat_room.service.ChatRoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat-rooms")
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(@Valid @RequestBody ChatRoomRegisterRequest createRequest,
                                                           BindingResult bindingResult) {

        Long userId = 1L; //TODO userId 인증 인가 끝나면 가져와야함
        ChatRoomResponse chatRoomResponse = chatRoomService.createChatRoom(createRequest, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(chatRoomResponse);
    }


}