package org.mocka.jsonbind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.bson.types.ObjectId;

public class ObjectIdSerializer extends StdSerializer<ObjectId> {

    public ObjectIdSerializer() {
        super(ObjectId.class);
    }

    @Override
    public void serialize(ObjectId value, JsonGenerator g, SerializerProvider provider) throws IOException {
        g.writeString(value.toString());
    }
}
