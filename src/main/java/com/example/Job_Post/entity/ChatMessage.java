package com.example.Job_Post.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@Table(indexes = {
    @Index(name = "idx_chat_message_room_time", columnList = "chat_room_id,timestamp"),
    @Index(name = "idx_chat_message_recipient_read_sender", columnList = "recipient_id,is_read,sender_id")
})
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    private Integer id;

    // private String chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;


    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = true)
    private User recipient;

    private String content;

    private Instant timestamp;

    @Builder.Default
    private Boolean isRead = false; // Indicates if the message has been read by the recipient

    @Builder.Default   
    private Boolean isSystemGenerated = false;

    

    @PrePersist
    public void onSend() {
        if (timestamp == null) timestamp = Instant.now();
        chatRoom.setLastMessageAt(timestamp);
    }

}
