package com.example.Job_Post.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Job_Post.dto.FileMapper;
import com.example.Job_Post.entity.File;
import com.example.Job_Post.entity.User;
import com.example.Job_Post.repository.FileRepository;
import com.example.Job_Post.service.FileUploadService;
import com.example.Job_Post.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {
    private final UserService userService;
    private final FileUploadService fileUploadService;
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;


    @PostMapping("/uploadFile/{entity}/{entityId}/{field}")
    public ResponseEntity<?> uploadFile(
            @PathVariable String entity,
            @PathVariable Integer entityId,
            @PathVariable String field,
            @RequestParam("file") MultipartFile file,
            Principal principal
    ) {
        try {

            User user = userService.getUserByEmail(principal.getName());
            if (user == null || !user.getId().equals(entityId) || !entity.equals("user") || !field.equals("profile")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to upload file for this user.");
            } // We will handle other entities and fields later


            String newFileName = FileUploadService.generateFileName(entity, entityId, field);
            String url = fileUploadService.upload(file, newFileName, "auto");

            File newFile = File.builder()
                .fileName(file.getOriginalFilename())
                .fileUrl(url)
                .uploadedAt(LocalDateTime.now())
                .size(file.getSize() / 1024.0) // size in KB
                .user(user)
                .isActive(true)
                .build();

            File savedFile = fileRepository.save(newFile);

            return ResponseEntity.ok(fileMapper.toDto(savedFile));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }                         
    }


    @DeleteMapping("/deleteFile/{fileId}")
    public ResponseEntity<?> deleteFile(@PathVariable Integer fileId, Principal principal){
        try {

            User user = userService.getUserByEmail(principal.getName());
            
            File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId) );

            if (!user.equals(file.getUser())){
                throw new IllegalAccessError("This user does not have permission to change the file");
            }

            file.setUser(null);
            fileRepository.save(file);

            // fileRepository.delete(file);


            return ResponseEntity.ok("File deleted successfully");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to delete the file: " + e.getMessage());
        }




    }

    @PutMapping("/setActive/{fileId}")
    public ResponseEntity<?> setActive(@PathVariable Integer fileId, @RequestParam Boolean trueIfActive, Principal principal){
        try {

            User user = userService.getUserByEmail(principal.getName());
            
            File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found with id: " + fileId) );

            if (!user.equals(file.getUser())){
                throw new IllegalAccessError("This user does not have permission to change the file");
            }
            
            file.setIsActive(trueIfActive);
            File savedFile = fileRepository.save(file);

            return ResponseEntity.ok(savedFile);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Unable to edit the file: " + e.getMessage());
        }




    }




}
