package org.mocka.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.bson.types.ObjectId;
import org.mocka.properties.MongoProperties;
import org.mocka.properties.MongoProperties.Credentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.lang.NonNull;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMongoRepositories(basePackages = {"org.mocka.domain"})
public class MongoClientConfiguration extends AbstractMongoClientConfiguration {

    private final MongoProperties mongo;

    @PostConstruct
    private void init() {
        var randomValue = new ObjectId(0, 0).toHexString().substring(8, 18);
        log.info("ObjectId process random value: {}", randomValue);
    }


    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }


    @NonNull
    @Override
    protected String getDatabaseName() {
        return mongo.getDatabase();
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.applyConnectionString(new ConnectionString(mongo.getUrl()));
        builder.uuidRepresentation(UuidRepresentation.STANDARD);
        var credentials = Optional.ofNullable(mongo.getCredentials());
        var username = credentials.map(Credentials::getUsername);
        var password = credentials.map(Credentials::getPassword).map(String::toCharArray);
        if (username.isPresent() && password.isPresent()) {
            builder.credential(MongoCredential.createCredential(
                username.get(),
                getDatabaseName(),
                password.get()
            ));
        }
    }
}
