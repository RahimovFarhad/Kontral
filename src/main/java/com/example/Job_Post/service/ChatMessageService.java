package com.example.Job_Post.service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.Job_Post.entity.ChatMessage;
import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.enumerator.ChatState;
import com.example.Job_Post.repository.ChatMessageRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    @Transactional
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        if (chatMessage.getSender() == null || chatMessage.getRecipient() == null || chatMessage.getContent() == null || chatMessage.getContent().isEmpty()) {
            throw new IllegalArgumentException("Sender, Recipient, and content must not be null or empty");
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(chatMessage.getSender(), chatMessage.getRecipient(), true)
            .orElseThrow(() -> new IllegalStateException("Chat room could not be created"));

        ChatState chatState = chatRoom.getChatState();
        if (ChatState.BLOCKED.equals(chatState)) {
            throw new IllegalStateException("Messages are blocked in this chat");
        }

        // if (ChatState.REQUEST_PENDING.equals(chatState)
        //     && !chatMessage.getSender().getId().equals(chatRoom.getRequestInitiatorId())) {
        //     chatRoom.setChatState(ChatState.ACTIVE);
        //     chatRoom.setBlockedByUserId(null);
        // }

        if (ChatState.REQUEST_PENDING.equals(chatRoom.getChatState())
                && chatRoom.getRequestInitiatorId() != null
                && !chatMessage.getSender().getId().equals(chatRoom.getRequestInitiatorId())) {
            throw new IllegalStateException("Chat request must be accepted before replying");
        }

        chatMessage.setChatRoom(chatRoom);
        chatMessage.setTimestamp(Instant.now());
        chatMessage.setIsRead(false); // Default to unread

        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatMessages (Integer senderID, Integer recipientID){
        return getChatMessages(senderID, recipientID, null, null);
    }

    public List<ChatMessage> getChatMessages(
        Integer senderID,
        Integer recipientID,
        Instant before,
        Integer limit
    ) {
        if (senderID == null || recipientID == null) {
            throw new IllegalArgumentException("Sender ID and Recipient ID must not be null");
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(senderID, recipientID, false)
            .orElseThrow(() -> new IllegalStateException("Chat room not found"));

        if (chatRoom == null) {
            throw new IllegalArgumentException("Chat room not found");
        }
        int resolvedLimit = (limit == null) ? 50 : limit;
        if (resolvedLimit < 1 || resolvedLimit > 200) {
            throw new IllegalArgumentException("Limit must be between 1 and 200");
        }

        PageRequest pageRequest = PageRequest.of(0, resolvedLimit);
        List<ChatMessage> messages = (before == null)
            ? chatMessageRepository.findByChatRoomOrderByTimestampDesc(chatRoom, pageRequest)
            : chatMessageRepository.findByChatRoomAndTimestampLessThanOrderByTimestampDesc(chatRoom, before, pageRequest);

        Collections.reverse(messages);
        return messages;
    }

    public ChatMessage getChatMessageById(Integer id) {
        return chatMessageRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Chat Message not found"));
    }

    public void setMessageIsRead(Integer messageId) {
        ChatMessage msg = chatMessageRepository.getChatMessageByIdLightweight(messageId);
        msg.setIsRead(true);
        chatMessageRepository.save(msg);
    }

}
