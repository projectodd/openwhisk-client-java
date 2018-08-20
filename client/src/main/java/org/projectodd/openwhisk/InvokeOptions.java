package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.KeyValue;

import java.util.Map;

/**
 * Represents the options to apply to function invocations.
 */
public class InvokeOptions {
    private final String name;
    private boolean blocking;
    private boolean results;
    private int timeout = 30000;
    private KeyValue params = new KeyValue();

    public InvokeOptions(final String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }

    public boolean blocking() {
        return blocking;
    }

    public InvokeOptions blocking(final boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    public boolean results() {
        return results;
    }

    public InvokeOptions results(final boolean results) {
        this.results = results;
        return this;
    }

    public InvokeOptions parameter(String key, Object value) {
        params.put(key, value);
        return this;
    }

    public InvokeOptions parameters(Map<String, Object> values) {
//        params.addAll(values.entrySet().stream()
//              .map( e -> new KeyValue().key(e.getKey()).value(e.getValue()))
//              .collect(Collectors.toList()));
        params.putAll(values);
        return this;
    }

    KeyValue parameters() {
        return params;
    }

    public int timeout() {
        return timeout;
    }

    public InvokeOptions timeout(final int timeout) {
        this.timeout = timeout;
        return this;
    }
}
