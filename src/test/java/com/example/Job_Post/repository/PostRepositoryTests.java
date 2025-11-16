package com.example.Job_Post.repository;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.Job_Post.entity.Post;
import com.example.Job_Post.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

public class PostRepositoryTests {

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private UserRepository userRepository;


    @Test
    public void PostRepository_Save_ReturnsSavedPost() {
        //Arrange
        User postUser = User.builder().email("fff").password("hhh").build();
        userRepository.save(postUser);
        Post post = Post
                        .builder()
                        .creator(postUser)
                        .title("postTitle")
                        .build();
        //Act

        Post savedPost = postRepository.save(post);

        //Assert

        Assertions.assertThat(savedPost).isNotNull();
        Assertions.assertThat(savedPost.getId()).isGreaterThan(0);

    }


    @Test
    public void PostRepository_Edit_ReturnsPost() {
        //Arrange
        User postUser = User.builder().email("fff").password("hhh").build();
        userRepository.save(postUser);
        Post post = Post
                        .builder()
                        .creator(postUser)
                        .title("postTitle")
                        .build();

        postRepository.save(post);

        //Act


        final String newTitle = "newTitle";

        Post getPost = postRepository.findById(post.getId()).get(); 
        String firstTitle = getPost.getTitle();

        getPost.setTitle(newTitle);
        Post editedPost = postRepository.save(getPost);
        

        //Assert
        Assertions.assertThat(editedPost).isNotNull();
        
        Assertions.assertThat(editedPost.getTitle()).isNotEqualTo(firstTitle);
        Assertions.assertThat(editedPost.getTitle()).isEqualTo(newTitle);

    }


    @Test
    public void PostRepository_FindById_ReturnsPost() {
        //Arrange
        User postUser = User.builder().email("fff").password("hhh").build();
        userRepository.save(postUser);
        Post post = Post
                        .builder()
                        .creator(postUser)
                        .title("postTitle")
                        .build();

        postRepository.save(post);

        //Act

        Post getPost = postRepository.findById(post.getId()).get(); 

        //Assert
        Assertions.assertThat(getPost).isNotNull();

    }

}