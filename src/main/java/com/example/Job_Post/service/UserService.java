package com.example.Job_Post.service;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.ChatUserDTO;
import com.example.Job_Post.dto.UserDTO;
import com.example.Job_Post.dto.UserMapper;
import com.example.Job_Post.entity.ChatRoom;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.ChatRelationshipStatus;
import com.example.Job_Post.enumerator.ChatState;
import com.example.Job_Post.enumerator.PreferredRole;
import com.example.Job_Post.enumerator.Status;
import com.example.Job_Post.repository.ChatMessageRepository;
import com.example.Job_Post.repository.ChatRoomRepository;
import com.example.Job_Post.repository.NotificationRepository;
import com.example.Job_Post.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ChatMessageRepository chatMessageRepository;
    private final NotificationRepository notificationRepository;
    private final CurrentUser cUser;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRelationshipTagService userRelationshipTagService;

    public User connectUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        user.setStatus(Status.ONLINE); // Set the user's status to ONLINE

        System.out.println("Connecting user: " + userEmail);
        return userRepository.save(user); // Save the updated user to the database
    }

    public User disconnectUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        user.setStatus(Status.OFFLINE); // Set the user's status to OFFLINE
        return userRepository.save(user); // Save the updated user to the database
    }

    public List<User> findConnectedUsers() {
        List<User> users = userRepository.findAllByStatus(Status.ONLINE);
        if (users == null || users.isEmpty()) {
            throw new IllegalArgumentException("No connected users found");
        }
        return users;
    }

    public User getUserById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        User user = userRepository.findById(id).
            orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        if (!user.getVerified()){
            throw new IllegalArgumentException("User is not verified");
        }
        return user;
            
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public User changeRating(String method, User user, Integer ratingNew, Integer ratingBefore){
        double averageRating = user.getAverageRating() == null ? 0.0 : user.getAverageRating();
        int ratingCount = user.getRatingCount() == null ? 0 : user.getRatingCount();
        int newRatingValue = ratingNew == null ? 0 : ratingNew;
        int beforeRatingValue = ratingBefore == null ? 0 : ratingBefore;

        double sum;

        switch (method.strip().toLowerCase()) {
            case "add":
                sum = averageRating*ratingCount + newRatingValue;
                ratingCount += 1;
                break;
            
            case "remove":
                sum = averageRating*ratingCount - beforeRatingValue;
                ratingCount = Math.max(0, ratingCount - 1);
                break;
            
            case "change":
                sum = averageRating*ratingCount - beforeRatingValue + newRatingValue;
                break;
        
            default:
                throw new IllegalArgumentException("Invalid rating update method: " + method);

        }

        if (ratingCount > 0)
            averageRating = sum/ratingCount;
        else
            averageRating = 0.0;


        user.setAverageRating(averageRating);
        user.setRatingCount(ratingCount);


        return userRepository.save(user);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAllWithSkillsAndFiles();
        if (users.isEmpty()) {
            return List.of();
        }
        List<Integer> userIds = users.stream().map(User::getId).toList();
        Map<Integer, Integer> unreadNotificationCounts = toCountMap(notificationRepository.countUnreadByUserIds(userIds));
        Map<Integer, Integer> unreadChatCounts = toCountMap(chatMessageRepository.countUnreadByRecipientIds(userIds));

        return users.stream()
                    .map(user -> {
                        UserDTO dto = userMapper.toDTOWithoutLiveCounts(user);
                        dto.setNewNotificationCount(unreadNotificationCounts.getOrDefault(user.getId(), 0));
                        dto.setNewChatMessageCount(unreadChatCounts.getOrDefault(user.getId(), 0));
                        return dto;
                    })
                    .toList();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User changePreferredRole(User user, String preferredRoleValue) {
        PreferredRole preferredRole = PreferredRole.fromValue(preferredRoleValue);
        user.setPreferredRole(preferredRole);
        user.setUpdated_at(Instant.now());
        return userRepository.save(user);
    }

    public boolean isNickNameTaken(String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return false;
        }

        return userRepository.existsByNickNameIgnoreCase(nickName.trim());
    }

    public boolean isNickNameTakenByAnotherUser(String nickName, Integer currentUserId) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return false;
        }

        return userRepository.existsByNickNameIgnoreCaseAndIdNot(nickName.trim(), currentUserId);
    }

    public List<ChatUserDTO> getChatUsers() {
        return getMyChatUsers("all");
    }

    public List<ChatUserDTO> getChatUsers(String include) {
        return getMyChatUsers(include);
    }

    public List<ChatUserDTO> getMyChatUsers() {
        return getMyChatUsers("all");
    }

    public List<ChatUserDTO> getMyChatUsers(String include) {
        User currentUser = cUser.get();
        String includeValue = include == null ? "all" : include.trim().toLowerCase();

        Set<Integer> sendersWithUnread =
            chatMessageRepository.findSendersWithUnreadMessages(currentUser.getId());

        List<ChatRoom> visibleRooms = chatRoomRepository.findVisibleChatRoomsForUser(currentUser.getId());
        Map<Integer, Boolean> pendingInitiatedByCurrentUserByOtherUserId = new HashMap<>();

        for (ChatRoom room : visibleRooms) {
            Integer otherUserId = room.getUser1().getId().equals(currentUser.getId())
                ? room.getUser2().getId()
                : room.getUser1().getId();
            ChatState state = resolveChatState(room.getChatState());
            boolean pendingInitiatedByCurrentUser =
                ChatState.REQUEST_PENDING.equals(state)
                && currentUser.getId().equals(room.getRequestInitiatorId());
            pendingInitiatedByCurrentUserByOtherUserId.put(otherUserId, pendingInitiatedByCurrentUser);
        }

        List<ChatUserDTO> users = visibleRooms
                .stream()
                .map( (ChatRoom chatRoom) -> {
                    User otherUser = chatRoom.getUser1().getId().equals(currentUser.getId())
                            ? chatRoom.getUser2()
                            : chatRoom.getUser1();

                    ChatUserDTO dto = userMapper.toChatDTO(otherUser, false);
                    dto.setChatState(resolveChatState(chatRoom.getChatState()));
                    return dto;
                })
                .toList();

        Map<Integer, ChatRelationshipStatus> relationshipByUserId =
            userRelationshipTagService.getRelationshipMapForCurrentUser(
                currentUser.getId(),
                users.stream().map(ChatUserDTO::getId).toList()
            );
        
        for (ChatUserDTO u : users) {
            if (sendersWithUnread.size() == 0) {
                u.setRelationship(
                    relationshipByUserId.getOrDefault(u.getId(), ChatRelationshipStatus.NONE)
                );
                continue;
            }

            if (sendersWithUnread.contains(u.getId())) {
                u.setHasUnseenMessageToCurrentUser(true);
                sendersWithUnread.remove(u.getId());
            }

            u.setRelationship(
                relationshipByUserId.getOrDefault(u.getId(), ChatRelationshipStatus.NONE)
            );
            
        }

        if ("active".equals(includeValue)) {
            return users.stream()
                .filter(user ->
                    ChatState.ACTIVE.equals(user.getChatState())
                    || Boolean.TRUE.equals(pendingInitiatedByCurrentUserByOtherUserId.get(user.getId()))
                )
                .toList();
        }
        if ("pending".equals(includeValue)) {
            return users.stream()
                .filter(user ->
                    ChatState.REQUEST_PENDING.equals(user.getChatState())
                    && !Boolean.TRUE.equals(pendingInitiatedByCurrentUserByOtherUserId.get(user.getId()))
                )
                .toList();
        }
        if ("blocked".equals(includeValue)) {
            return users.stream()
                .filter(user -> ChatState.BLOCKED.equals(user.getChatState()))
                .toList();
        }

        return users;
        
    }

    private ChatState resolveChatState(ChatState state) {
        return state == null ? ChatState.ACTIVE : state;
    }

    private Map<Integer, Integer> toCountMap(List<Object[]> rows) {
        Map<Integer, Integer> map = new HashMap<>();
        for (Object[] row : rows) {
            Integer key = (Integer) row[0];
            Integer value = ((Number) row[1]).intValue();
            map.put(key, value);
        }
        return map;
    }

    
    
}
