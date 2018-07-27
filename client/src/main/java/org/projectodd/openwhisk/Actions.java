package org.projectodd.openwhisk;

import org.projectodd.openwhisk.api.ActionsApi;
import org.projectodd.openwhisk.model.Activation;
import org.projectodd.openwhisk.model.KeyValue;

import java.util.Collections;
import java.util.Map;

public class Actions {

    private OWskClient OWskClient;

    public Actions(final OWskClient OWskClient) {
        this.OWskClient = OWskClient;
    }

    /**
     * Create a new action
     */
    public void create() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

    /**
     * Update an existing action, or create an action if it does not exist
     */
    public void update() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

    /**
     * Invoke an action
     *
     * @param name the name of the action
     *
     * @return the invocation response
     */
    public <T> T invoke(final String name) {
        return invoke(name, Collections.emptyMap(), new InvokeOptions());
    }

    /**
     * Invoke an action
     *
     * @param name the name of the action
     * @param params the parameters to pass to the action
     *
     * @return the invocation response
     */
    public <T> T invoke(final String name, final Map<String, String> params) {
        return invoke(name, params, new InvokeOptions());
    }

    /**
     * Invoke an action
     *
     * @param name the name of the action
     * @param params the parameters to pass to the action
     * @param options the options to apply
     *
     * @return the invocation response
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(final String name, final Map<String, String> params, InvokeOptions options) {
        final Activation activation = new ActionsApi(OWskClient.getClient())
                                          .invokeAction("_", name, keyValue(params), options.blocking(),
                                              false, 30000);
        if(options.results()) {
            return (T) activation.getResponse().getResult();
        } else if(options.blocking()) {
            return (T) activation;
        } else {
            return (T) activation.getActivationId();
        }
    }

    private KeyValue keyValue(final Map<String, String> map) {
        final KeyValue keyValue = new KeyValue();
        keyValue.putAll(map);
        return keyValue;
    }

    /**
     * Get an action
     */
    public void get() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

    /**
     * Delete action
     */
    public void delete() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

    /**
     * List all actions in a namespace or actions contained in a package
     */
    public void list() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

}
