package com.example.dosirakbe.domain.message.dto.mapper;

import com.example.dosirakbe.domain.chat_room.entity.ChatRoom;
import com.example.dosirakbe.domain.message.dto.response.MessageResponse;
import com.example.dosirakbe.domain.message.entity.Message;
import com.example.dosirakbe.domain.message.entity.MessageType;
import com.example.dosirakbe.domain.user.entity.User;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-23T18:52:12+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponse mapToMessageResponse(Message message) {
        if ( message == null ) {
            return null;
        }

        Long userId = null;
        Long chatRoomId = null;
        MessageResponse beforeMessage = null;
        Long id = null;
        String content = null;
        MessageType messageType = null;
        LocalDateTime createdAt = null;

        userId = messageUserUserId( message );
        chatRoomId = messageChatRoomId( message );
        beforeMessage = mapToMessageResponse( message.getBeforeMessage() );
        id = message.getId();
        content = message.getContent();
        messageType = message.getMessageType();
        createdAt = message.getCreatedAt();

        MessageResponse messageResponse = new MessageResponse( id, content, messageType, createdAt, userId, chatRoomId, beforeMessage );

        return messageResponse;
    }

    private Long messageUserUserId(Message message) {
        if ( message == null ) {
            return null;
        }
        User user = message.getUser();
        if ( user == null ) {
            return null;
        }
        Long userId = user.getUserId();
        if ( userId == null ) {
            return null;
        }
        return userId;
    }

    private Long messageChatRoomId(Message message) {
        if ( message == null ) {
            return null;
        }
        ChatRoom chatRoom = message.getChatRoom();
        if ( chatRoom == null ) {
            return null;
        }
        Long id = chatRoom.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}