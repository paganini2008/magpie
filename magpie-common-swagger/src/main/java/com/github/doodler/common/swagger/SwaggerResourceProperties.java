package com.github.doodler.common.swagger;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: SwaggerResourceProperties
 * @Author: Fred Feng
 * @Date: 15/11/2022
 * @Version 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties("spring.application.swagger")
public class SwaggerResourceProperties {

    private String title = "Doodler API Documentation";
    private String description = "Doodler API Documentation";
    private String termsOfServiceUrl = "";
    private Contact contact = new Contact();
    private String license = "Apache Software";
    private String licenseUrl = "http://www.apache.org/licenses/LICENSE-2.0";
    private String version = "1.0.0-RELEASE";
    private List<String> api = new ArrayList<>();
    private List<String> path = new ArrayList<>();
    private List<String> excludedPath = new ArrayList<>();

    private Authorization authorization = new Authorization();

    @Getter
    @Setter
    public static class Contact {

        private String name = "Fred Feng";
        private String url = "";
        private String email = "paganini.fy@gmail.com";
    }

    @Getter
    @Setter
    public static class Authorization {

        private String name = "Authorization";
        private List<AuthorizationScope> authorizationScopeList = new ArrayList<>();
        private List<String> path = new ArrayList<>();
        private List<String> excludedPath = new ArrayList<>();
        private List<String> tokenUrl = new ArrayList<>();
        private String mode = "default";
    }

    @Getter
    @Setter
    public static class AuthorizationScope {

        private String scope = "";
        private String description = "";
    }
}
