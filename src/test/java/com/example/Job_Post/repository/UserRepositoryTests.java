// package com.example.Job_Post.repository;

// import java.util.List;
// import java.util.Optional;

// import org.assertj.core.api.Assertions;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
// import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
// import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

// import com.example.Job_Post.entity.User;

// @DataJpaTest
// @AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

// public class UserRepositoryTests {

//     @Autowired
//     private UserRepository userRepository;

//     // @Test
//     // public void UserRepository_Save_ReturnsSavedUser() {
//     //     //Arrange
//     //     User user = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();
//     //     //Act

//     //     User savedUser = userRepository.save(user);

//     //     //Assert

//     //     Assertions.assertThat(savedUser).isNotNull();
//     //     Assertions.assertThat(savedUser.getId()).isGreaterThan(0);

//     // }

//     // @Test
//     // public void UserRepository_FindAll_ReturnsAllUsers() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     User user2 = User
//     //                     .builder()
//     //                     .email("nazrin.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);
//     //     userRepository.save(user2);

//     //     List<User> allUsers = userRepository.findAll(); 

//     //     //Assert
//     //     Assertions.assertThat(allUsers).isNotNull();
//     //     Assertions.assertThat(allUsers.size()).isEqualTo(2);

        

//     // }

//     // @Test
//     // public void UserRepository_FindById_ReturnsUser() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);

//     //     User getUser = userRepository.findById(user1.getId()).get(); 

//     //     //Assert
//     //     Assertions.assertThat(getUser).isNotNull();

//     // }

//     // @Test
//     // public void UserRepository_FindByEmail_ReturnsUser() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);

//     //     User getUser = userRepository.findByEmail(user1.getEmail()).get(); 

//     //     //Assert
//     //     Assertions.assertThat(getUser).isNotNull();

//     // }

//     // @Test
//     // public void UserRepository_CheckExistsByEmail_ReturnsBoolean() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);

//     //     Boolean userExists = userRepository.existsByEmail(user1.getEmail()); 
//     //     Boolean userExists2 = userRepository.existsByEmail("non_existing_email@mail.ru"); 

//     //     //Assert
//     //     Assertions.assertThat(userExists).isTrue();

//     //     Assertions.assertThat(userExists2).isFalse();
//     // }


//     // @Test
//     // public void UserRepository_Edit_ReturnsUser() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);

//     //     final String newEmail = "new_email@mail.ru";

//     //     User getUser = userRepository.findById(user1.getId()).get(); 
//     //     String firstEmail = getUser.getEmail();

//     //     getUser.setEmail(newEmail);
//     //     User editedUser = userRepository.save(user1);
        

//     //     //Assert
//     //     Assertions.assertThat(editedUser).isNotNull();
        
//     //     Assertions.assertThat(editedUser.getEmail()).isNotEqualTo(firstEmail);
//     //     Assertions.assertThat(editedUser.getEmail()).isEqualTo(newEmail);

//     // }

//     // @Test
//     // public void UserRepository_Delete_ReturnsVoid() {
//     //     //Arrange
//     //     User user1 = User
//     //                     .builder()
//     //                     .email("farhad.r@mail.ru")
//     //                     .password("mmm123")
//     //                     .build();

//     //     //Act

//     //     userRepository.save(user1);

//     //     userRepository.delete(user1);

//     //     Optional<User> deletedUser = userRepository.findById(user1.getId());
        

//     //     //Assert
//     //     Assertions.assertThat(deletedUser).isEmpty();



//     // }
    
// }
