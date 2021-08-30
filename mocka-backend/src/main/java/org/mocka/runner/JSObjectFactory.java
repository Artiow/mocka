package org.mocka.runner;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.openjdk.nashorn.api.scripting.JSObject;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JSObjectFactory {

    private final JSObject jsObject;


    public static JSObjectFactory.Evaluator evaluator(ScriptEngine engine) {
        return new JSObjectFactory.Evaluator(engine);
    }

    public JSObject newObject() {
        return (JSObject) jsObject.newObject();
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Evaluator {

        private final ScriptEngine engine;


        public JSObjectFactory eval() throws ScriptException {
            return new JSObjectFactory((JSObject) engine.eval("Object"));
        }
    }
}
