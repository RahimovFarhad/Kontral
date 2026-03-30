package com.example.Job_Post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.PostImages;

public interface PostImagesRepository extends JpaRepository<PostImages, Integer> {
    List<PostImages> findByPostIdInOrderByCreatedAtAsc(List<Integer> postIds);
}
