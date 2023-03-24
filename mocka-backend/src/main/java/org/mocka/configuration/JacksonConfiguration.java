package org.mocka.configuration;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.mocka.jsonbind.ObjectIdDeserializer;
import org.mocka.jsonbind.ObjectIdSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfiguration {

    @Bean
    public com.fasterxml.jackson.databind.Module mongoModule() {
        var module = new SimpleModule();
        module.addSerializer(ObjectId.class, new ObjectIdSerializer());
        module.addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        return module;
    }
}
