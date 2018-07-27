package org.projectodd.openwhisk;

/**
 * Represents the options to apply to function invocations.
 */
public class InvokeOptions {
    private boolean blocking;
    private boolean results;

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
}
