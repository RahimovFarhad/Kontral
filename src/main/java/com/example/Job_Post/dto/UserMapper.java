package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.Company;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.Status;
import com.example.Job_Post.repository.ChatMessageRepository;
import com.example.Job_Post.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final SkillMapper skillMapper;
    private final FileMapper fileMapper;
    private final NotificationRepository notificationRepository;
    private final ChatMessageRepository chatMessageRepository;


    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO userDTO;
        if (user instanceof Company) {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setCompanyName(((Company) user).getCompanyName());
            companyDTO.setWebsite(((Company) user).getWebsite());
            companyDTO.setDescription(((Company) user).getDescription());
            userDTO = companyDTO;
            userDTO.setIsCompany(true);
        } else {
            userDTO = new UserDTO();
        }
        userDTO.setId(user.getId());
        userDTO.setNickName(user.getNickName() != null ? user.getNickName() : user.getEmail());
        userDTO.setEmail(user.getEmail());
        userDTO.setStatus(user.getStatus().toString());
        userDTO.setAboutMe(user.getAboutMe());
        userDTO.setProfileImage(user.getImageUrl());
        userDTO.setAverageRating(user.getAverageRating());
        userDTO.setCreatedAt(user.getCreated_at());
        userDTO.setUpdatedAt(user.getUpdated_at());
        userDTO.setSkills(user.getSkills().stream().map(skill -> new SkillMapper().toDTO(skill)).toList());
        userDTO.setFiles(user.getFiles().stream().map(file -> new FileMapper().toDto(file)).toList());
        userDTO.setNumber(user.getPhoneNumber());
        userDTO.setNewNotificationCount(notificationRepository.countByNotifiedUserIdAndIsReadFalse(user.getId()));
        userDTO.setNewChatMessageCount(chatMessageRepository.countByRecipientIdAndIsReadFalse(user.getId()));
        userDTO.setLinkedIn(user.getLinkedIn());

        

        return userDTO;
    }

    public ChatUserDTO toChatDTO(User user, Boolean hasUnseenMessage) {
        if (user == null) {
            return null;
        }

        // Map normally first
        UserDTO baseDto = toDTO(user);

        // Create ChatUserDTO and copy fields
        ChatUserDTO chatDto = new ChatUserDTO();
        chatDto.setId(baseDto.getId());
        chatDto.setNickName(baseDto.getNickName());
        chatDto.setEmail(baseDto.getEmail());
        chatDto.setStatus(baseDto.getStatus());
        chatDto.setAboutMe(baseDto.getAboutMe());
        chatDto.setProfileImage(baseDto.getProfileImage());
        chatDto.setAverageRating(baseDto.getAverageRating());
        chatDto.setCreatedAt(baseDto.getCreatedAt());
        chatDto.setUpdatedAt(baseDto.getUpdatedAt());
        chatDto.setSkills(baseDto.getSkills());
        chatDto.setFiles(baseDto.getFiles());
        chatDto.setNumber(baseDto.getNumber());
        chatDto.setNewNotificationCount(baseDto.getNewNotificationCount());
        chatDto.setNewChatMessageCount(baseDto.getNewChatMessageCount());
        chatDto.setLinkedIn(baseDto.getLinkedIn());

        // Extra property
        chatDto.setHasUnseenMessageToCurrentUser(hasUnseenMessage);
            

        return chatDto;
    }

    

    public User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        
        if (userDTO.getEmail() == null) {
            throw new IllegalArgumentException("Email cannot be null");
        }
        
        User user;

        if (userDTO.getIsCompany() != null && userDTO.getIsCompany()) {
            Company company = new Company();
            company.setCompanyName(((CompanyDTO) userDTO).getCompanyName());
            company.setWebsite(((CompanyDTO) userDTO).getWebsite());
            company.setDescription(((CompanyDTO) userDTO).getDescription());
            user = company;
        } else {
            user = new User();
        }
        
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setNickName(userDTO.getNickName());
        user.setAboutMe(userDTO.getAboutMe());
        user.setImageUrl(userDTO.getProfileImage());
        user.setAverageRating(userDTO.getAverageRating());
        user.setCreated_at(userDTO.getCreatedAt());
        user.setUpdated_at(userDTO.getUpdatedAt());
        user.setPhoneNumber(userDTO.getNumber());
        user.setLinkedIn(userDTO.getLinkedIn());

        // Handle status conversion (assuming Status is an enum)
        if (userDTO.getStatus() != null) {
            user.setStatus(Status.valueOf(userDTO.getStatus().toUpperCase()));
        }
        
        // Handle skills mapping
        if (userDTO.getSkills() != null) {
            user.setSkills(userDTO.getSkills().stream()
                .map(skillDTO -> skillMapper.toEntity(skillDTO))
                .toList());
        }
        
        // Handle files mapping
        if (userDTO.getFiles() != null) {
            user.setFiles(userDTO.getFiles().stream()
                .map(fileDTO -> fileMapper.toEntity(fileDTO))
                .toList());
        }

        
        
        return user;
    }

    public static User updateEntityFromDTO(UserDTO userDTO, User user) {
        if (userDTO == null || user == null) {
            throw new IllegalArgumentException("UserDTO and User cannot be null");
        }

        user.setNickName(userDTO.getNickName());
        user.setAboutMe(userDTO.getAboutMe());
        user.setLinkedIn(userDTO.getLinkedIn());
        // user.setSkills(userDTO.getSkills());
        

        return user;
    }
    
}


