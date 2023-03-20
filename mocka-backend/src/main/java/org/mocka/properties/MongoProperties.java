package org.mocka.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("mongo")
public class MongoProperties {

    private String url;
    private String database;
    private Credentials credentials;

    @Getter
    @Setter
    public static class Credentials {

        private String username;
        private String password;
    }
}
