package com.company.java_basic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) return null;
        // 파일명 충돌 방지
        String ext = getExt(file.getOriginalFilename());
        String key = folder + "/" + UUID.randomUUID() + ext;

        try {
            PutObjectRequest req = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(req, RequestBody.fromBytes(file.getBytes()));
            // ✅ “공개 URL” 형태로 반환 (버킷이 public 읽기 가능하거나 CloudFront 쓰는 전제)
            return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;

        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }

    private String getExt(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf(".");
        if (dot == -1) return "";
        return filename.substring(dot); // ".png"
    }
}
