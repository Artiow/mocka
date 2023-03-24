package org.mocka.jsonbind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bson.types.ObjectId;

public class ObjectIdDeserializer extends StdDeserializer<ObjectId> {

    public ObjectIdDeserializer() {
        super(ObjectId.class);
    }

    @Override
    public ObjectId deserialize(JsonParser p, DeserializationContext ctxt) {
        return new ObjectId(p.currentToken().asString());
    }
}
