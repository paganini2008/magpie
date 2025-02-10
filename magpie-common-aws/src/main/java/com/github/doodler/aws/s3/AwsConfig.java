package com.github.doodler.aws.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import com.github.doodler.aws.s3.AwsCredentialProperties.Credentials;
import io.awspring.cloud.s3.S3Template;
import io.awspring.cloud.sns.core.SnsTemplate;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 
 * @Description: AwsConfig
 * @Author: Fred Feng
 * @Date: 06/01/2025
 * @Version 1.0.0
 */
@EnableConfigurationProperties({AwsCredentialProperties.class})
@Configuration(proxyBeanMethods = false)
public class AwsConfig {

    @Autowired
    private AwsCredentialProperties awsCredentialProperties;

    @Bean("s3Client")
    @Profile("local")
    public S3Client localS3Client() {
        Credentials credentials = awsCredentialProperties.getCredentials();
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(credentials.getAccessKey(), credentials.getSecretKey()));
        return S3Client.builder().region(Region.of(credentials.getRegion()))
                .credentialsProvider(credentialsProvider).build();
    }

    @Bean
    public S3Service s3Service(S3Template s3Template) {
        return new S3Service(s3Template);
    }

    @Bean
    public SnsService snsService(SnsTemplate snsTemplate) {
        return new SnsService(snsTemplate);
    }

    @Bean
    public SqsService sqsService(SqsTemplate sqsTemplate) {
        return new SqsService(sqsTemplate);
    }

}
