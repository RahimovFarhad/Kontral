package com.example.Job_Post.dto;

import org.springframework.stereotype.Component;

import com.example.Job_Post.entity.File;

@Component

public class FileMapper {

    public FileDTO toDto(File file) {
        return FileDTO.builder()
                .id(file.getId())
                .name(file.getFileName())
                .url(file.getFileUrl())
                // .size(file.getFileSize())
                .uploadedAt(file.getUploadedAt())
                .type(file.getFileName() != null && file.getFileName().contains(".") ?
                        file.getFileName().substring(file.getFileName().lastIndexOf(".") + 1) : "unknown")
                .size(file.getSize())
                .isActive(file.getIsActive())
                .build();
    }

    public File toEntity(FileDTO fileDTO) {
        return File.builder()
                .id(fileDTO.getId())
                .fileName(fileDTO.getName())
                .fileUrl(fileDTO.getUrl())
                // .fileSize(fileDTO.getSize())
                .build();
    }

}
