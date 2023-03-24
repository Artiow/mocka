package org.mocka.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.mocka.properties.MongoProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = {"org.mocka.domain"})
public class MongoClientConfiguration extends AbstractMongoClientConfiguration {

    private final MongoProperties mongo;

    @PostConstruct
    private void init() {
        log.info("ObjectId process random value: {}", new ObjectId(0, 0).toHexString().substring(8, 18));
    }

    @NonNull
    @Override
    protected String getDatabaseName() {
        return mongo.getDatabase();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder
                .applyConnectionString(new ConnectionString(
                        mongo.getUrl()))
                .credential(MongoCredential.createCredential(
                        mongo.getCredentials().getUsername(),
                        getDatabaseName(),
                        mongo.getCredentials().getPassword().toCharArray()));
    }
}
