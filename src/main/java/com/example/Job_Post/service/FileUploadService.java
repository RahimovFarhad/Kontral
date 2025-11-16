package com.example.Job_Post.service;

// Import the required packages

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {
    // Set your Cloudinary credentials

    Dotenv dotenv = Dotenv.load();
    Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));

    @SuppressWarnings("rawtypes")
    public String upload(MultipartFile file, String customFileName, String resource_type) throws IOException {
            // Upload the image
            Map params1 = ObjectUtils.asMap(
                "public_id", customFileName,
                "unique_filename", true,
                "overwrite", true,
                "unique_filename", true,
                "folder", "gig_spot",
                "resource_type", resource_type
            );

            Map uploadedImage = cloudinary.uploader().upload(file.getBytes(), params1);
            
            return (String) uploadedImage.get("secure_url");
        }

    public static String generateFileName(String entity, Integer entityId, String field) {
        // Timestamp
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // UUID (to avoid collisions)
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        // Final name: e.g. user/42/profile_20250825163000_ab12cd34.jpg
        return String.format("%s/%d/%s_%s_%s",
                entity,
                entityId,
                field,
                timestamp,
                uniqueId
        );
    }
    
}
