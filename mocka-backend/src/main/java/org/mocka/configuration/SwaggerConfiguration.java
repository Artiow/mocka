package org.mocka.configuration;

import lombok.RequiredArgsConstructor;
import org.mocka.properties.ApiInfoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ApiInfoProperties.class)
public class SwaggerConfiguration implements WebMvcConfigurer {

    private final ApiInfoProperties apiInfo;


    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(new ApiInfo(
                        apiInfo.getTitle(),
                        apiInfo.getDescription(),
                        apiInfo.getVersion(),
                        ApiInfo.DEFAULT.getTermsOfServiceUrl(),
                        ApiInfo.DEFAULT_CONTACT,
                        ApiInfo.DEFAULT.getLicense(),
                        ApiInfo.DEFAULT.getLicenseUrl(),
                        ApiInfo.DEFAULT.getVendorExtensions()
                ))
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.mocka.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // noinspection SpringMVCViewInspection
        registry.addViewController("/").setViewName("redirect:/swagger-ui/");
    }
}
