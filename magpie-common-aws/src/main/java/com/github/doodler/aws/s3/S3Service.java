package com.github.doodler.aws.s3;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import org.apache.commons.io.IOUtils;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.exception.SdkClientException;

/**
 * 
 * @Description: S3Service
 * @Author: Fred Feng
 * @Date: 06/01/2025
 * @Version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Template s3Template;

    public String createBucket(String bucketName) {
        return s3Template.createBucket(bucketName);
    }

    public S3Resource uploadFile(String bucketName, String path, File file) throws IOException {
        try (InputStream ins = Files.newInputStream(file.toPath())) {
            return uploadFile(bucketName, path, ins);
        }
    }

    public S3Resource uploadFile(String bucketName, String path, InputStream ins)
            throws SdkClientException, IOException {
        return s3Template.upload(bucketName, path, ins);
    }

    public List<S3Resource> listObjects(String bucketName, String prefix) {
        return s3Template.listObjects(bucketName, prefix);
    }

    public S3Resource download(String bucketName, String path) {
        return s3Template.download(bucketName, path);
    }

    public void download(String bucketName, String path, File file) throws IOException {
        try (OutputStream os = Files.newOutputStream(file.toPath())) {
            S3Resource s3Resource = download(bucketName, path);
            IOUtils.copy(s3Resource.getInputStream(), os);
            os.flush();
        }
    }

    public void download(String bucketName, String path, OutputStream os) throws IOException {
        S3Resource s3Resource = download(bucketName, path);
        IOUtils.copy(s3Resource.getInputStream(), os);
        os.flush();
    }

    public void deleteObject(String bucketName, String path) {
        s3Template.deleteObject(bucketName, path);
    }

    public void deleteBucket(String bucketName) {
        s3Template.deleteBucket(bucketName);
    }

}
