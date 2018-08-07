package org.projectodd.openwhisk;

import org.projectodd.openwhisk.api.ActionsApi;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.Activation;
import org.projectodd.openwhisk.model.ActivationResponse;

import java.util.Collections;
import java.util.Map;

public class Actions {

    private OWskClient client;

    public Actions(final OWskClient client) {
        this.client = client;
    }

    /**
     * Create a new action
     *
     * @param name
     * @param exec
     * @throws ApiException if an action with the given name already exists
     */
    public ActionPut create(final String name, final ActionExec exec) {
        return update(name, new ActionPut().exec(exec), false);
    }

    /**
     * Create a new action
     *
     * @param name
     * @param actionPut
     */
    public ActionPut create(final String name, final ActionPut actionPut) {
        return update(name, actionPut, false);
    }

    /**
     * Update an action
     *
     * @param name
     * @param exec
     */
    public ActionPut update(final String name, final ActionExec exec) {
        return update(name, new ActionPut().exec(exec), false);
    }

    /**
     * Update an existing action, or create an action if it does not exist
     */
    public ActionPut update(final String name, final ActionPut actionPut, boolean overwrite) {
        ActionPut put = Utils.encodeFile(actionPut);

        final ActionPut result = new ActionsApi(client.getClient())
                                     .updateAction(client.getConfiguration().getNamespace(), name, put, overwrite);

        result.getExec().code(actionPut.getExec().getCode());
        return result;
    }

    /**
     * Invoke an action
     *
     * @param name the name of the action
     * @return the invocation response
     */
    public <T> T invoke(final String name) {
        return invoke(name, Collections.emptyMap(), new InvokeOptions());
    }

    /**
     * Invoke an action
     *
     * @param name   the name of the action
     * @param params the parameters to pass to the action
     * @return the invocation response
     */
    public <T> T invoke(final String name, final Map<String, String> params) {
        return invoke(name, params, new InvokeOptions());
    }

    /**
     * Invoke an action
     *
     * @param name    the name of the action
     * @param params  the parameters to pass to the action
     * @param options the options to apply
     * @return the invocation response
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(final String name, final Map<String, String> params, InvokeOptions options) {
        final Activation activation = new ActionsApi(client.getClient())
                                          .invokeAction(client.getConfiguration().getNamespace(), name, Utils.keyValue(params),
                                              options.blocking(), false, 30000);
        if (options.results()) {
            final ActivationResponse response = activation.getResponse();
            return (T) response.getResult();
        } else if (options.blocking()) {
            return (T) activation;
        } else {
            return (T) activation.getActivationId();
        }
    }

    /**
     * Get an action
     */
    public void get() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

    /**
     * Delete action
     *
     * @param name the name of the action to delete
     * @throws ApiException if the named action does not exist
     */
    public void delete(final String name) {
        new ActionsApi(client.getClient())
            .deleteAction(client.getConfiguration().getNamespace(), name);
    }

    /**
     * List all actions in a namespace or actions contained in a package
     */
    public void list() {
        throw new UnsupportedOperationException("method not yet implemented");
    }

}
