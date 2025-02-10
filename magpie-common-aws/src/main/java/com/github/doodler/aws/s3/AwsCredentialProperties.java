package com.github.doodler.aws.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Data;

/**
 * 
 * @Description: AwsCredentialProperties
 * @Author: Fred Feng
 * @Date: 06/01/2025
 * @Version 1.0.0
 */
@ConfigurationProperties("spring.cloud.aws")
@Data
public class AwsCredentialProperties {

    private Credentials credentials = new Credentials();

    /**
     * 
     * @Description: Credentials
     * @Author: Fred Feng
     * @Date: 06/01/2025
     * @Version 1.0.0
     */
    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
        private String region;
    }

}
