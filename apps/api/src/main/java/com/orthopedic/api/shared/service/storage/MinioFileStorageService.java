package com.orthopedic.api.shared.service.storage;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Profile("dev")
@RequiredArgsConstructor
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            return uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize(), folder);
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String filename, String contentType, long size, String folder) {
        try {
            String fileKey = folder + "/" + UUID.randomUUID() + "-" + filename;
            
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .stream(inputStream, size, -1)
                    .contentType(contentType)
                    .build()
            );
            
            return fileKey;
        } catch (Exception e) {
            log.error("Failed to upload stream to MinIO", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public Resource downloadFile(String fileKey) {
        try {
            InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            log.error("Failed to download file from MinIO", e);
            throw new RuntimeException("File download failed", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileKey)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO", e);
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucket)
                    .object(fileKey)
                    .expiry(7, TimeUnit.DAYS)
                    .build()
            );
        } catch (Exception e) {
            log.error("Failed to get file URL from MinIO", e);
            return null;
        }
    }
}
