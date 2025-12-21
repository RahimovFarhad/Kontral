package com.example.Job_Post.service;


import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.Job_Post.component.CurrentUser;
import com.example.Job_Post.dto.ChatUserDTO;
import com.example.Job_Post.dto.UserDTO;
import com.example.Job_Post.dto.UserMapper;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.Status;
import com.example.Job_Post.repository.ChatMessageRepository;
import com.example.Job_Post.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final ChatMessageRepository chatMessageRepository;
    private final CurrentUser cUser;

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
        double averageRating = user.getAverageRating();
        int ratingCount = user.getRatingCount();

        double sum;

        switch (method.strip().toLowerCase()) {
            case "add":
                sum = averageRating*ratingCount + ratingNew;
                ratingCount += 1;
                break;
            
            case "remove":
                sum = averageRating*ratingCount - ratingBefore;
                ratingCount -= 1;
                break;
            
            case "change":
                sum = averageRating*ratingCount - ratingBefore + ratingNew ;
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
        List<User> users = userRepository.findAll();
        return users.stream()
                    .map(user -> userMapper.toDTO(user))
                    .toList();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<ChatUserDTO> getChatUsers() {
        User currentUser = cUser.get();

        Set<Integer> sendersWithUnread =
                chatMessageRepository.findSendersWithUnreadMessages(currentUser.getId());

        List<ChatUserDTO> users = userRepository.findAllChatUsersLight();

        // Set the unseen message flag
        for (ChatUserDTO u : users) {
            u.setHasUnseenMessageToCurrentUser(
                    sendersWithUnread.contains(u.getId())
            );
        }

        return users;
    }

    
    
}
