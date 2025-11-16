package com.example.Job_Post.service;

import static org.mockito.Mockito.when;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.Job_Post.auth.AuthenticationResponse;
import com.example.Job_Post.auth.RegisterRequest;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.enumerator.AuthMethod;
import com.example.Job_Post.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class RegisterServiceTests {

    // @Mock
    // private UserRepository userRepository;

    
    // @Mock
    // private PasswordEncoder passwordEncoder; // Add this mock
    
    // @InjectMocks
    // private RegisterService registerService;



    // @Test
    // public void RegisterService_RegisterUserCustomAuth_ReturnsRegisteredUser() {
    //     // Arrange
    //     // Create a mock user object and set expectations on the userRepository
    //     RegisterRequest registerRequest = RegisterRequest
    //                     .builder()
    //                     .email("farhad.r@mail.ru")
    //                     .password("mmm123")
    //                     .build();

    //     User user = new User();
    //     user.setEmail(registerRequest.getEmail());
    //     user.setPassword(registerRequest.getPassword());


    //     when(userRepository.existsByEmail(Mockito.anyString())).thenReturn(false);
    //     when(userRepository.existsByPhoneNumber(Mockito.any())).thenReturn(false);
    //     when(passwordEncoder.encode(Mockito.anyString())).thenReturn("encoded-password");
    //     when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        
    //     // Mockito.doReturn("mocked-jwt-token").when(jwtService).generateToken(Mockito.any(User.class));

    //     // Act
    //     // Call the register method of registerService with the mock user

    //     User authenticationResponse = registerService.register(registerRequest, AuthMethod.Custom);

    //     // Assert
    //     // Verify that the userRepository.save method was called with the correct user
    //     Assertions.assertNotNull(authenticationResponse);
    // }    

}
