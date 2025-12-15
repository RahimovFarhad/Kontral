package com.example.Job_Post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.Job_Post.dto.UserWebSocketDTO;
import com.example.Job_Post.enumerator.AuthMethod;
import com.example.Job_Post.enumerator.Role;
import com.example.Job_Post.enumerator.Status;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE )
    // @Column(unique = true)
    private Integer id;

    private String verificationTokenHash;
    private LocalDateTime verificationTokenExpiry;
    private Boolean verified;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private ResetToken resetToken;


    private String nickName;

    private String aboutMe;
    private String imageUrl;

    private String linkedIn;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Skill> skills = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<File> files = new HashSet<>();

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<JobApplication> jobApplications = new HashSet<>();

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phoneNumber;

    @Builder.Default
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at;
    private String password;

    private Double averageRating;
    private Integer ratingCount;
    

    @Enumerated(EnumType.STRING)            
    private Role role;

    @Enumerated(EnumType.STRING)
    private AuthMethod authMethod; // Assuming AuthMethod is an enum or class that you have defined elsewhere

    @Enumerated(EnumType.STRING)
    private Status status; // Indicates if the user account is online



    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY)
    private List<Review> receivedReviews;


    @OneToMany(mappedBy = "writer", fetch = FetchType.LAZY)
    private List<Review> writtenReviews;

    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Post> posts;


    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> sentMessages;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> receivedMessage;
    

    // @OneToMany(mappedBy = "notifiedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Notification> receivedNotifications;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }


    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return Objects.equals(this.id, other.id) && Objects.equals(this.email, other.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }


    @Transient
    public UserWebSocketDTO toWebSocketDTO() {
        return new UserWebSocketDTO(this.id, this.email, this.nickName, this.status);
    }
    
}
