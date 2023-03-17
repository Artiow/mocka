package org.mocka.configuration;

import lombok.RequiredArgsConstructor;
import org.mocka.properties.CorsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(CorsProperties.class)
public class WebConfiguration implements WebMvcConfigurer {

    private final CorsProperties cors;


    @Bean
    public FilterRegistrationBean<LightCommonsRequestLoggingFilter> requestLoggingFilter() {
        var register = new FilterRegistrationBean<>(new LightCommonsRequestLoggingFilter());
        register.setOrder(Integer.MAX_VALUE);
        return register;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(cors.getAllowedOrigins())
                .allowedMethods(cors.getAllowedMethods());
    }


    private static class LightCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {

        public LightCommonsRequestLoggingFilter() {
            super();
            this.setBeforeMessagePrefix("[");
            this.setAfterMessagePrefix("[");
            this.setIncludeClientInfo(false);
            this.setIncludeQueryString(true);
            this.setIncludePayload(false);
            this.setIncludeHeaders(false);
        }

        @Override
        protected void afterRequest(@NonNull HttpServletRequest request, @NonNull String message) {

        }
    }
}
