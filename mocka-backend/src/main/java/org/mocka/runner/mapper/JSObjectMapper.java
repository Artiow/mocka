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

    private final static TypeReference<Map<String, Object>> OBJECT_TYPE_REFERENCE = new TypeReference<>() { };
    private final static TypeReference<Collection<Object>> ARRAY_TYPE_REFERENCE = new TypeReference<>() { };

    private final ObjectMapper objectMapper;
    private final ThreadLocal<JSObjectFactory> jsObjectFactoryThreadLocal;


    public JSObjectMapper(ObjectMapper objectMapper, ScriptEngine engine) {
        this.objectMapper = objectMapper;
        this.jsObjectFactoryThreadLocal = ThreadLocal.withInitial(Suppliers.sneaky(JSObjectFactory.evaluator(engine)));
    }


    // todo: Turn mapper into Jackson deserializer, make extends
    //       com.fasterxml.jackson.databind.deser.std.ContainerDeserializerBase.
    public Object map(Object obj) throws JSObjectMapperException {
        try {
            return buildValue(toRaw(obj));
        } catch (Exception e) {
            throw new JSObjectMapperException("Exception occurred while object mapping", e);
        } finally {
            // todo: Create pool of JSObjectFactory instances
            //       to prevent JSObjectFactory evaluating per thread.
            discardJSObjectFactory();
        }
    }


    private Object toRaw(Object obj) throws IllegalArgumentException {
        if (isValue(obj)) return obj; // ready to use
        if (isArray(obj)) return toRawArray(obj);
        return toRawObject(obj);
    }

    private Map<String, Object> toRawObject(Object obj) throws IllegalArgumentException {
        return objectMapper.convertValue(obj, OBJECT_TYPE_REFERENCE);
    }

    private Collection<Object> toRawArray(Object obj) throws IllegalArgumentException {
        return objectMapper.convertValue(obj, ARRAY_TYPE_REFERENCE);
    }

    private boolean isValue(Object obj) {
        return obj == null || ClassUtils.isPrimitiveWrapper(obj.getClass());
    }

    private boolean isArray(Object obj) {
        return obj instanceof Collection || obj.getClass().isArray();
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

    private void discardJSObjectFactory() {
        jsObjectFactoryThreadLocal.remove();
    }
}
