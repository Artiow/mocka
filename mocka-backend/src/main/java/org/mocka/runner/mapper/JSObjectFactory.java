package org.mocka.runner.mapper;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.mocka.util.ThrowingSupplier;
import org.openjdk.nashorn.api.scripting.JSObject;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JSObjectFactory {

    private final JSObject objectConstructor;
    private final JSObject arrayConstructor;


    public static JSObjectFactory.Evaluator evaluator(ScriptEngine engine) {
        return new JSObjectFactory.Evaluator(engine);
    }


    public JSObject newObject() {
        return (JSObject) objectConstructor.newObject();
    }

    public JSObject newArray() {
        return (JSObject) arrayConstructor.newObject();
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Evaluator implements ThrowingSupplier<JSObjectFactory> {

        private static final String SCRIPT = "Object({objectConstructor: Object, arrayConstructor: Array})";
        private static final ScriptContext CONTEXT = new SimpleScriptContext();

        private final ScriptEngine engine;


        @Override
        public JSObjectFactory get() throws ScriptException {
            return evaluate();
        }

        public JSObjectFactory evaluate() throws ScriptException {
            var jsObjectFactory = (JSObject) engine.eval(SCRIPT, CONTEXT);
            return new JSObjectFactory(
                (JSObject) jsObjectFactory.getMember("objectConstructor"),
                (JSObject) jsObjectFactory.getMember("arrayConstructor")
            );
        }
    }
}
