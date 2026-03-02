package com.orthopedic.api.shared.service.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String folder);
    String uploadFile(InputStream inputStream, String filename, String contentType, long size, String folder);
    Resource downloadFile(String fileKey);
    void deleteFile(String fileKey);
    String getFileUrl(String fileKey);
}
