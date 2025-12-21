package com.example.Job_Post.service;

import java.time.LocalDateTime;

import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.Job_Post.auth.RegisterRequest;
import com.example.Job_Post.config.JwtService;
import com.example.Job_Post.entity.ResetToken;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.ResetTokenRepository;
import com.example.Job_Post.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import com.example.Job_Post.enumerator.AuthMethod;
import com.example.Job_Post.enumerator
.Role;
import com.example.Job_Post.enumerator.Status;
import com.example.Job_Post.enumerator.TokenType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;

    private final ResetTokenRepository resetTokenRepository;

    private static final int verificationTokenByteLength = 32;
    private static final int verificationTokenExpiryMinute = 30;

    

    //I should create a edit method to update user details
    //I should create a delete method to delete user details
    //I should simplify the register method's [updateToCustom] by using edit method which is yet to be created

    // This method is disabled for now: Free render deploy does not support email sending
    // public User register(RegisterRequest request, AuthMethod authMethod) throws IllegalArgumentException {
    //     User user = null;
    //     Boolean updatesToCustom = false;

    //     if (request == null) {
    //         throw new IllegalArgumentException("Request cannot be null");
    //     }
    //     if (authMethod == AuthMethod.Custom && (request.getPassword() == null || request.getPassword().isEmpty())) {
    //         throw new IllegalArgumentException("Password is required for custom registration");
    //     }

    //     request.setEmail(request.getEmail().toLowerCase()); // set all emails to lowercase
    //     Boolean existsByEmail = userRepository.existsByEmail(request.getEmail() != null ? request.getEmail() : "");

        
    //     if (existsByEmail) {
    //         user = userRepository.findByEmail(request.getEmail())
    //         .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

    //         // If the email exists and the auth method is Custom, we throw an error
    //         if (user.getAuthMethod().equals(AuthMethod.Custom) && authMethod.equals(AuthMethod.Custom)) {
    //             if (user.getVerified())
    //                 throw new IllegalArgumentException("Email already exists");
    //             else if(user.getVerificationTokenHash() != null && !LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())){
    //                 throw new IllegalArgumentException("A Verification Link Already Sent");
    //             }
    //         }
    //         // If the email exists but the auth method is OAuth2, we can proceed without throwing an error
    //         if (authMethod.equals(AuthMethod.Custom)) {
    //             updatesToCustom = true;
    //         }

    //         user.setAuthMethod(AuthMethod.Custom);
    //         user.setPassword(passwordEncoder.encode(request.getPassword()));
    //         user.setNickName(null);
    //         user.setPhoneNumber(request.getPhoneNumber());  
    //         user.setUpdated_at(LocalDateTime.now()); // Assuming you have an updated_at field in User
    //     }

    //     Boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : "");
        
    //     if (!updatesToCustom && existsByPhoneNumber) {
    //         throw new IllegalArgumentException("Phone number already exists");
    //     }
    //     else if (!updatesToCustom && !existsByEmail && !existsByPhoneNumber) {
    //         user = User.builder()
    //                             .nickName(null) // Nickname can be set later")
    //                             .updated_at(LocalDateTime.now()) // Assuming you have an updated_at field in User
    //                             .phoneNumber(request.getPhoneNumber())
    //                             .email(request.getEmail())
    //                             .role(Role.USER) // Assuming Role is an enum with USER as one of the values
    //                             .authMethod(authMethod) // Assuming authMethod is a field in User
    //                             .password((authMethod.equals(AuthMethod.Custom) ?  passwordEncoder.encode(request.getPassword()) : null)) // Encode password only if authMethod is Custom
    //                             .build();
            
    //     }

    //     user.setStatus(Status.OFFLINE);

    //     User savedUser = null;

    //     if (user.getAuthMethod() == AuthMethod.Custom){
    //         final String verificationToken = verificationTokenService.generateToken(verificationTokenByteLength);
    //         user.setVerificationTokenHash(passwordEncoder.encode(verificationToken));
    //         user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(verificationTokenExpiryMinute));
    //         user.setVerified(false);

    //         savedUser = userRepository.save(user);

    //         emailService.sendVerificationEmail(user.getEmail(), verificationToken);

    //     }
    //     else{
    //         user.setVerified(true);
    //         savedUser = userRepository.save(user);
    //     }

    //     return savedUser;
                                      

    // }


    public User register(RegisterRequest request, AuthMethod authMethod) throws IllegalArgumentException {
        User user = null;
        Boolean updatesToCustom = false;

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (authMethod == AuthMethod.Custom && (request.getPassword() == null || request.getPassword().isEmpty())) {
            throw new IllegalArgumentException("Password is required for custom registration");
        }

        request.setEmail(request.getEmail().toLowerCase()); // set all emails to lowercase
        Boolean existsByEmail = userRepository.existsByEmail(request.getEmail() != null ? request.getEmail() : "");

        
        if (existsByEmail) {
            user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + request.getEmail()));

            // If the email exists and the auth method is Custom, we throw an error
            if (user.getAuthMethod().equals(AuthMethod.Custom) && authMethod.equals(AuthMethod.Custom)) {
                if (user.getVerified())
                    throw new IllegalArgumentException("Email already exists");
                else if(user.getVerificationTokenHash() != null && !LocalDateTime.now().isAfter(user.getVerificationTokenExpiry())){
                    throw new IllegalArgumentException("A Verification Link Already Sent");
                }
            }
            // If the email exists but the auth method is OAuth2, we can proceed without throwing an error
            if (authMethod.equals(AuthMethod.Custom)) {
                updatesToCustom = true;
            }

            user.setAuthMethod(AuthMethod.Custom);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setNickName(null);
            user.setPhoneNumber(request.getPhoneNumber());  
            user.setUpdated_at(LocalDateTime.now()); // Assuming you have an updated_at field in User
        }

        Boolean existsByPhoneNumber = userRepository.existsByPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber() : "");
        
        if (!updatesToCustom && existsByPhoneNumber) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        else if (!updatesToCustom && !existsByEmail && !existsByPhoneNumber) {
            user = User.builder()
                                .nickName(null) // Nickname can be set later")
                                .updated_at(LocalDateTime.now()) // Assuming you have an updated_at field in User
                                .phoneNumber(request.getPhoneNumber())
                                .email(request.getEmail())
                                .role(Role.USER) // Assuming Role is an enum with USER as one of the values
                                .authMethod(authMethod) // Assuming authMethod is a field in User
                                .password((authMethod.equals(AuthMethod.Custom) ?  passwordEncoder.encode(request.getPassword()) : null)) // Encode password only if authMethod is Custom
                                .build();
            
        }

        user.setStatus(Status.OFFLINE);

        User savedUser = null;

        if (user.getAuthMethod() == AuthMethod.Custom){
            final String verificationToken = verificationTokenService.generateToken(verificationTokenByteLength);
            user.setVerificationTokenHash(passwordEncoder.encode(verificationToken));
            user.setVerificationTokenExpiry(LocalDateTime.now().plusMinutes(verificationTokenExpiryMinute));
            user.setVerified(true); // temporary because free render does not support email sending

            savedUser = userRepository.save(user);
        }
        else{
            user.setVerified(true);
            savedUser = userRepository.save(user);
        }

        return savedUser;
                                      

    }

    public void registerOAuth(RegisterRequest request, AuthMethod authMethod, HttpServletResponse response){
        User user = register(request, authMethod);


        String refreshToken = JwtService.generateToken(user, TokenType.REFRESH);


        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) // localhost
                    .sameSite("None") // required for cross-origin
                    .path("/api/v1/auth/refresh")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

        response.setHeader("Set-Cookie", cookie.toString());



        return;

    }

    public User saveVerifiedUser(User user){
        user.setVerified(true);
        user.setVerificationTokenHash(null);
        user.setVerificationTokenExpiry(null);
        return userRepository.save(user);
    }

    public User changePassword(User user, String newPassword){
        user.setPassword(passwordEncoder.encode(newPassword)); // Encode password only if authMethod is Custom
        user.setAuthMethod(AuthMethod.Custom);
        return userRepository.save(user);

    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        final String resetTokenHash = verificationTokenService.generateToken(verificationTokenByteLength);

        ResetToken resetToken;

        if (resetTokenRepository.existsByUserEmail(email)){
            resetToken = resetTokenRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Reset token not found for email: " + email));
            resetToken.setTokenHash(passwordEncoder.encode(resetTokenHash));
            resetToken.setExpiryTime(LocalDateTime.now().plusMinutes(verificationTokenExpiryMinute));
            resetTokenRepository.save(resetToken);
            emailService.sendForgotPasswordEmail(user.getEmail(), resetTokenHash);
            return;
        }
        else{
            resetToken = ResetToken.builder()
                .tokenHash(passwordEncoder.encode(resetTokenHash))
                .user(user)
                .expiryTime(LocalDateTime.now().plusMinutes(verificationTokenExpiryMinute))
                .build();
        }
        

        resetTokenRepository.save(resetToken);

        emailService.sendForgotPasswordEmail(user.getEmail(), resetTokenHash);

    }

    



    
}


