package org.mocka.runner.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openjdk.nashorn.api.scripting.JSObject;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import java.util.Collections;
import java.util.Map;

@Service
public class JSObjectMapper {

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<>() { };

    private final JSObjectFactory.Evaluator factoryEvaluator;


    public JSObjectMapper(ScriptEngine engine) {
        this.factoryEvaluator = JSObjectFactory.evaluator(engine);
    }


    public synchronized JSObject map(Object obj) throws JSObjectMapperException {
        try {
            var rawObj = createRaw(obj);
            // todo: create and use ready thread-safe JSObjectFactory
            return buildJSObject(rawObj, factoryEvaluator.eval());
        } catch (Exception e) {
            throw new JSObjectMapperException("Exception occurred while object mapping", e);
        }
    }


    private Map<String, Object> createRaw(Object obj) {
        return isValue(obj)
                ? Collections.singletonMap("value", obj)
                : OBJECT_MAPPER.convertValue(obj, MAP_TYPE_REFERENCE);
    }

    private JSObject buildJSObject(Map<?, ?> rawObj, JSObjectFactory jsObjectFactory) {
        var jsObject = jsObjectFactory.newObject();
        rawObj.forEach((k, v) -> {
            validateMapKey(k);
            validateMapValue(v);
            jsObject.setMember(
                    (String) k,
                    isObject(v) ? buildJSObject((Map<?, ?>) v, jsObjectFactory) : v
            );
        });
        return jsObject;
    }


    private void validateMapKey(Object key) {
        if (!(key instanceof String)) {
            throw new IllegalArgumentException(String.format("Key type \"%s\" is unacceptable", key.getClass().getName()));
        }
    }

    private void validateMapValue(Object value) {
        if (!(isValue(value) || isObject(value))) {
            throw new IllegalArgumentException(String.format("Value type \"%s\" is unacceptable", value.getClass().getName()));
        }
    }


    private boolean isValue(Object value) {
        return value == null || value.getClass().isPrimitive() || value instanceof String;
    }

    private boolean isObject(Object value) {
        return value instanceof Map;
    }
}
