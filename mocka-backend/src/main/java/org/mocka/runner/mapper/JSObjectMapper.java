package org.mocka.runner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;
import java.util.Map;
import javax.script.ScriptEngine;
import org.mocka.util.Suppliers;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

@Service
public class JSObjectMapper {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static TypeReference<Map<String, Object>> OBJECT_TYPE_REFERENCE = new TypeReference<>() { };
    private final static TypeReference<Collection<Object>> ARRAY_TYPE_REFERENCE = new TypeReference<>() { };

    private final ThreadLocal<JSObjectFactory> jsObjectFactoryThreadLocal;


    public JSObjectMapper(ScriptEngine engine) {
        this.jsObjectFactoryThreadLocal = ThreadLocal.withInitial(Suppliers.sneaky(JSObjectFactory.evaluator(engine)));
    }


    private static Object toRaw(Object obj) throws IllegalArgumentException {
        if (isValue(obj)) return obj; // ready to use
        if (isArray(obj)) return toRawArray(obj);
        return toRawObject(obj);
    }

    private static Map<String, Object> toRawObject(Object obj) throws IllegalArgumentException {
        return OBJECT_MAPPER.convertValue(obj, OBJECT_TYPE_REFERENCE);
    }

    private static Collection<Object> toRawArray(Object obj) throws IllegalArgumentException {
        return OBJECT_MAPPER.convertValue(obj, ARRAY_TYPE_REFERENCE);
    }


    private static boolean isValue(Object obj) {
        return obj == null || ClassUtils.isPrimitiveWrapper(obj.getClass());
    }

    private static boolean isArray(Object obj) {
        return obj instanceof Collection || obj.getClass().isArray();
    }


    // todo: Turn mapper to Jackson deserializer, make extends
    //       com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase.
    public Object map(Object obj) throws JSObjectMapperException {
        try {
            return buildValue(toRaw(obj));
        } catch (Exception e) {
            throw new JSObjectMapperException("Exception occurred while object mapping", e);
        }
    }


    @SuppressWarnings("unchecked")
    private Object buildValue(Object v) {
        if (v instanceof Map) return buildObjectValue((Map<String, Object>) v);
        if (v instanceof Collection) return buildArrayValue((Collection<Object>) v);
        return v; // ready to use
    }

    private JSObject buildObjectValue(Map<String, Object> rawObjectValue) {
        var jsObject = newObject();
        rawObjectValue.forEach((key, value) -> jsObject.setMember(key, buildValue(value)));
        return jsObject;
    }

    private JSObject buildArrayValue(Collection<Object> rawArrayValue) {
        var jsObject = newArray();
        int index = 0; for (var element : rawArrayValue) { jsObject.setSlot(index++, buildValue(element)); }
        return jsObject;
    }


    private JSObject newObject() {
        return getJSObjectFactory().newObject();
    }

    private JSObject newArray() {
        return getJSObjectFactory().newArray();
    }

    private JSObjectFactory getJSObjectFactory() {
        return jsObjectFactoryThreadLocal.get();
    }
}
