package com.cloud.webapp.service.aws;

import com.cloud.webapp.exceptions.Types.FieldAlreadyExistsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
public class S3Service {
    private final S3Client s3Client;
    private final String bucketName;
    private final Region region;

    public S3Service(
            @Value("${aws.region}") String region,
            @Value("${aws.bucketName}") String bucketName) {
        this.region = Region.of(region);
        this.bucketName = bucketName;
        this.s3Client = S3Client.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(this.region)
                .build();
    }

    // Upload image to S3
    public String uploadImage(String s3ObjectKey, MultipartFile imageFile) {
        String folderPath = s3ObjectKey.substring(0, s3ObjectKey.lastIndexOf("/") + 1);

        // Check if any file already exists in the folder
        if (doesFolderContainFile(folderPath)) {
            throw new FieldAlreadyExistsException("File", "file",imageFile.getName() );
        }

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3ObjectKey)
                    .contentType(imageFile.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    software.amazon.awssdk.core.sync.RequestBody.fromBytes(imageFile.getBytes()));

            return getImageUrl(s3ObjectKey);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    // Get image URL
    public String getImageUrl(String s3ObjectKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region.id(), s3ObjectKey);
    }

    // Delete image from S3
    public void deleteImage(String s3ObjectKey) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(s3ObjectKey)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    // Check if folder contains any file
    private boolean doesFolderContainFile(String folderPath) {
        try {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .prefix(folderPath)
                    .maxKeys(1)
                    .build();
            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            return !listObjectsResponse.contents().isEmpty();
        } catch (SdkClientException e) {
            throw new RuntimeException("Error checking if folder contains files in S3", e);
        }
    }
}
