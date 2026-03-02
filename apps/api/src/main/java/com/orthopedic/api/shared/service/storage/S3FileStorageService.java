package com.orthopedic.api.shared.service.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

@Service
@Slf4j
@Profile("prod")
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Override
    public String uploadFile(MultipartFile file, String folder) {
        try {
            return uploadFile(file.getInputStream(), file.getOriginalFilename(), file.getContentType(), file.getSize(), folder);
        } catch (Exception e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public String uploadFile(InputStream inputStream, String filename, String contentType, long size, String folder) {
        try {
            String fileKey = folder + "/" + UUID.randomUUID() + "-" + filename;
            
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .contentType(contentType)
                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, size));
            
            return fileKey;
        } catch (Exception e) {
            log.error("Failed to upload stream to S3", e);
            throw new RuntimeException("File upload failed", e);
        }
    }

    @Override
    public Resource downloadFile(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
            return new InputStreamResource(s3Client.getObject(getObjectRequest));
        } catch (Exception e) {
            log.error("Failed to download file from S3", e);
            throw new RuntimeException("File download failed", e);
        }
    }

    @Override
    public void deleteFile(String fileKey) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Failed to delete file from S3", e);
        }
    }

    @Override
    public String getFileUrl(String fileKey) {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofDays(7))
                .getObjectRequest(getObjectRequest)
                .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Failed to get file URL from S3", e);
            return null;
        }
    }
}
