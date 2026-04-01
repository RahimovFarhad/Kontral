package com.example.Job_Post.dto;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.Company;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.PreferredRole;
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
        userDTO.setPreferredRole(resolvePreferredRole(user));
        userDTO.setIsFirstTimeAccessing(isFirstTimeAccessing(user));
        userDTO.setIsAccountComplete(isAccountComplete(user));

        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setContactNumber(user.getContactNumber());

        

        return userDTO;
    }

    public ChatUserDTO toChatDTO(User user, boolean hasUnseen) {
        if (user == null) return null;

        ChatUserDTO dto = new ChatUserDTO();
        dto.setId(user.getId());
        dto.setNickName(user.getNickName() != null ? user.getNickName() : user.getEmail());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus());
        dto.setHasUnseenMessageToCurrentUser(hasUnseen);
        dto.setImageUrl(user.getImageUrl());

        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setContactNumber(user.getContactNumber());

        return dto;
    }

    public UserDTO toSummaryDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNickName(user.getNickName() != null ? user.getNickName() : user.getEmail());
        dto.setEmail(user.getEmail());
        dto.setStatus(user.getStatus() != null ? user.getStatus().toString() : null);
        dto.setAboutMe(user.getAboutMe());
        dto.setProfileImage(user.getImageUrl());
        dto.setAverageRating(user.getAverageRating());
        dto.setCreatedAt(user.getCreated_at());
        dto.setUpdatedAt(user.getUpdated_at());
        dto.setLinkedIn(user.getLinkedIn());
        dto.setPreferredRole(resolvePreferredRole(user));
        dto.setIsCompany(user instanceof Company);
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setContactNumber(user.getContactNumber());
        return dto;
    }

    public UserDTO toDTOWithoutLiveCounts(User user) {
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
        userDTO.setLinkedIn(user.getLinkedIn());
        userDTO.setPreferredRole(resolvePreferredRole(user));
        userDTO.setIsFirstTimeAccessing(isFirstTimeAccessing(user));
        userDTO.setIsAccountComplete(isAccountComplete(user));
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setContactNumber(user.getContactNumber());
        return userDTO;
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
        if (userDTO.getPreferredRole() != null) {
            user.setPreferredRole(PreferredRole.fromValue(userDTO.getPreferredRole()));
        }

        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setContactNumber(userDTO.getContactNumber());

        // Handle status conversion (assuming Status is an enum)
        if (userDTO.getStatus() != null) {
            user.setStatus(Status.valueOf(userDTO.getStatus().toUpperCase()));
        }
        
        // Handle skills mapping
        if (userDTO.getSkills() != null) {
            user.setSkills(userDTO.getSkills().stream()
                .map(skillDTO -> skillMapper.toEntity(skillDTO))
                .collect(Collectors.toSet()));
        }
        
        // Handle files mapping
        if (userDTO.getFiles() != null) {
            user.setFiles(userDTO.getFiles().stream()
                .map(fileDTO -> fileMapper.toEntity(fileDTO))
                .collect(Collectors.toSet()));
        }

        
        
        return user;
    }

    private boolean isFirstTimeAccessing(User user) {
        return user.getUpdated_at() == null;
    }

    private boolean isAccountComplete(User user) {
        return !isBlank(user.getNickName())
            && !isBlank(user.getEmail())
            && !isBlank(user.getFirstName())
            && !isBlank(user.getLastName());
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static User updateEntityFromDTO(UserDTO userDTO, User user) {
        if (userDTO == null || user == null) {
            throw new IllegalArgumentException("UserDTO and User cannot be null");
        }

        user.setNickName(userDTO.getNickName());
        user.setAboutMe(userDTO.getAboutMe());
        user.setLinkedIn(userDTO.getLinkedIn());
        if (userDTO.getPreferredRole() != null) {
            user.setPreferredRole(PreferredRole.fromValue(userDTO.getPreferredRole()));
        }
        // user.setSkills(userDTO.getSkills());
        

        return user;
    }

    private String resolvePreferredRole(User user) {
        if (user.getPreferredRole() == null) {
            return PreferredRole.ALL.getApiValue();
        }
        return user.getPreferredRole().getApiValue();
    }
    
}
