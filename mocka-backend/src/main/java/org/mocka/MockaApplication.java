package org.mocka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan("org.mocka.properties")
@EnableScheduling
public class MockaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockaApplication.class, args);
    }
}
