package com.example.Job_Post.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Job_Post.entity.File;

public interface FileRepository extends JpaRepository<File, Integer>{

}
