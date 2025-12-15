// package com.example.Job_Post.service;

// import static org.mockito.Mockito.when;

// import java.nio.file.AccessDeniedException;
// import java.util.Optional;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.junit.jupiter.MockitoExtension;
// import org.springframework.security.authentication.AuthenticationManager;
// import com.example.Job_Post.auth.AuthenticationRequest;
// import com.example.Job_Post.auth.AuthenticationResponse;
// import com.example.Job_Post.entity.User;
// import com.example.Job_Post.enumerator.AuthMethod;
// import com.example.Job_Post.repository.UserRepository;

// @ExtendWith(MockitoExtension.class)
// public class AuthenticationServiceTests {

//     @Mock
//     private UserRepository userRepository;
    
//     @Mock
//     private AuthenticationManager authenticationManager; // Add this mock
    
//     @InjectMocks
//     private AuthenticationService authenticationService;



//     @Test
//     public void AuthenticationService_AuthenticateUser_ReturnsToken() throws AccessDeniedException {
//         // Arrange
//         // Create a mock user object and set expectations on the userRepository
//         AuthenticationRequest authenticationRequest = AuthenticationRequest
//                         .builder()
//                         .email("farhad.r@mail.ru")
//                         .password("mmm123")
//                         .build();

//         User user = new User();
//         user.setEmail(authenticationRequest.getEmail());
//         user.setPassword(authenticationRequest.getPassword());
//         user.setAuthMethod(AuthMethod.Custom);


//         when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));
        
//         // Mockito.doReturn("mocked-jwt-token").when(jwtService).generateToken(Mockito.any(User.class));

//         // Act
//         // Call the register method of registerService with the mock user

//         AuthenticationResponse authenticationResponse = authenticationService.authenticate(authenticationRequest, null);

//         // Assert
//         // Verify that the userRepository.save method was called with the correct user
//         Assertions.assertNotNull(authenticationResponse);
//     }    

// }
