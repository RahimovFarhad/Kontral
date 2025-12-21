package com.example.Job_Post.controller;


import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Job_Post.auth.AuthenticationRequest;
import com.example.Job_Post.auth.RegisterRequest;
import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.dto.UserDTO;
import com.example.Job_Post.dto.UserMapper;
import com.example.Job_Post.entity.ResetToken;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.AuthMethod;
import com.example.Job_Post.service.AuthenticationService;
import com.example.Job_Post.service.FileUploadService;
import com.example.Job_Post.service.RegisterService;
import com.example.Job_Post.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;
    private final RegisterService registerService;

    private final UserService userService;
    private final UserMapper userMapper;

    private final SimpMessagingTemplate messagingTemplate;
    private final PasswordEncoder passwordEncoder;

    private final FileUploadService fileUploadService;

    @SuppressWarnings("unchecked")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {

        try {
            User user = registerService.register(request, AuthMethod.Custom);
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(userMapper.toDTO(user));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
        
    }

    // this endpoint is disabled for now: Free render deploy does not support email sending
    // @GetMapping("/register/verify")
    // public ResponseEntity<?> verifyEmail(@RequestParam("token") String token, @RequestParam("email") String email) {

    //     User user = userService.getUserByEmail(email);

    //     if (user == null){
    //         return ResponseEntity.badRequest().body("Invalid verification link.");
    //     }

    //     String storedHashedToken = user.getVerificationTokenHash();
    //     LocalDateTime expiry = user.getVerificationTokenExpiry();

    //     if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
    //         return ResponseEntity.badRequest().body("Verification link expired.");
    //     }

    //     if (!passwordEncoder.matches(token, storedHashedToken)) {
    //         return ResponseEntity.badRequest().body("Invalid verification token.");
    //     }

    //     registerService.saveVerifiedUser(user);

    //     return ResponseEntity.status(HttpStatus.CREATED).body("Email successfully verified!");
    // } 

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email){
        try {
            registerService.forgotPassword(email);
            return ResponseEntity.ok("Password reset email sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send password reset email: " + e.getMessage());
        }

    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<?> isResetTokenValid(@RequestParam("token") String token, @RequestParam("email") String email) {
        User user = userService.getUserByEmail(email);

        if (user == null){
            return ResponseEntity.badRequest().body("Invalid reset link.");
        }

        ResetToken storedToken = user.getResetToken();
        LocalDateTime expiry = storedToken.getExpiryTime();

        if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
            return ResponseEntity.badRequest().body("Verification link expired.");
        }

        if (!passwordEncoder.matches(token, storedToken.getTokenHash())) {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }

        return ResponseEntity.ok("Reset token is valid.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> changePassword(@RequestParam("token") String token, @RequestParam("email") String email, @RequestParam("newPassword") String newPassword) {

        try {
            User user = userService.getUserByEmail(email);
    
            if (user == null){
                return ResponseEntity.badRequest().body("Invalid reset link.");
            }
    
            ResetToken storedToken = user.getResetToken();
            LocalDateTime expiry = storedToken.getExpiryTime();
    
            if (expiry == null || LocalDateTime.now().isAfter(expiry)) {
                return ResponseEntity.badRequest().body("Verification link expired.");
            }
    
            if (!passwordEncoder.matches(token, storedToken.getTokenHash())) {
                return ResponseEntity.badRequest().body("Invalid verification token.");
            }
    
            registerService.changePassword(user, newPassword);

            user.setResetToken(null);
            userService.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body("Password changed successfully!");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to change password: " + e.getMessage());
        }
    } 
    

    @PostMapping(value = "/authenticate", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public void authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ) {
        try {
            authenticationService.authenticate(request, response);

            // IMPORTANT: redirect, do NOT return JSON
            response.sendRedirect("https://kontral.onrender.com");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @SuppressWarnings("unchecked")
    @PostMapping("/uploadImage/{entity}/{entityId}/{field}")
    public ResponseEntity<String> setUserProfileImage(
            @PathVariable String entity,
            @PathVariable Integer entityId,
            @PathVariable String field,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            if (user == null || !user.getId().equals(entityId) || !entity.equals("user") || !field.equals("profile")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to upload image for this user.");
            } // We will handle other entities and fields later

            String newFileName = FileUploadService.generateFileName(entity, entityId, field);
            String url = fileUploadService.upload(file, newFileName, "image");
            @SuppressWarnings("rawtypes")
            ResponseEntity res = ResponseEntity.ok(url);

            user.setImageUrl(url);
            userService.save(user);

            return res;
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }                         
    }

   

    @PutMapping("/updateMe")
    public ResponseEntity<?> updateMyUser(@RequestBody UserDTO userDTO, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            User userUpdated = UserMapper.updateEntityFromDTO(userDTO, user);
            
            userService.save(userUpdated);
            return ResponseEntity.ok(userMapper.toDTO(userUpdated));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update user: " + e.getMessage());
        }
    }

    @GetMapping("profile/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with id: " + id);
            }
            return ResponseEntity.ok(userMapper.toDTO(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve user: " + e.getMessage());
        }
    }


    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome to the Job Post API!");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve users: " + e.getMessage());
        }
    }

    @GetMapping("/chat/users")
    public ResponseEntity<?> getChatUsers() {
        try {
            return ResponseEntity.ok(userService.getChatUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to retrieve chat users: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyUser(Principal principal) {
        try {
            UserDTO user = userMapper.toDTO(userService.getUserByEmail(principal.getName()));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to retrieve users: " + e.getMessage());

        }
    }

    @MessageMapping("/user.addUser")
    // @SendTo("/user/topic")
    public UserDTO addUserToChat(@Header("Authorization") String token) {
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required");
        }

        final String jwt;
        final String username;

        jwt = token.substring(7); // Extract the JWT from the header 
        username = JwtService.extractUsername(jwt); // Extract username from the JWT

        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }

        User user = userService.connectUser(username);
        if (user == null) {
            throw new IllegalStateException("User not found or already connected");
        }

        UserDTO userDto = userMapper.toDTO(user);

        messagingTemplate.convertAndSendToUser(username, "/topic", userDto);

        return userDto;
    }

    @MessageMapping("/user.disconnectUser")
    public UserDTO disconnectUserFromChat(
        @Header("Authorization") String token) throws Exception {

        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Authorization token is required");
        }

        final String jwt;
        final String username;

        jwt = token.substring(7); // Extract the JWT from the header 
        username = JwtService.extractUsername(jwt); // Extract username from the JWT

        if (username == null) {
            throw new IllegalStateException("User not authenticated");
        }

        User user = userService.getUserByEmail(username);
        user = userService.disconnectUser(user.getEmail());

        UserDTO userDto = userMapper.toDTO(user);
        messagingTemplate.convertAndSendToUser(username, "/topic", userDto);

        return userDto;
    }

    @GetMapping("/connected-users")
    public ResponseEntity<?> getConnectedUsers() {
        try {
            return ResponseEntity.ok(userService.findConnectedUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching connected users: " + e.getMessage());
        }
    }
    
}
