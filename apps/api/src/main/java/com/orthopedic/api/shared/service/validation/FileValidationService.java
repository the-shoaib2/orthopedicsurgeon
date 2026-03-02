package com.orthopedic.api.shared.service.validation;

import com.orthopedic.api.shared.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class FileValidationService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
        "image/jpeg", "image/png", "application/pdf", 
        "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    public void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File size exceeds maximum limit of 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new BusinessException("Unsupported file type: " + contentType);
        }

        // 🔒 SECURITY: Basic filename sanitization
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains("..")) {
            throw new BusinessException("Invalid filename sequence");
        }
        
        // FUTURE: Integrate with ClamAV or similar for virus scanning
    }
}
