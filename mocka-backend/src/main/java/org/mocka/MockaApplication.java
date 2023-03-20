package org.mocka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.mocka.properties")
public class MockaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockaApplication.class, args);
    }
}
