package org.projectodd.openwhisk;

import org.projectodd.openwhisk.api.ActionsApi;
import org.projectodd.openwhisk.invoker.ApiException;
import org.projectodd.openwhisk.model.Action;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.Activation;
import org.projectodd.openwhisk.model.ActivationResponse;

public class Actions {

    private OWskClient client;

    Actions(final OWskClient client) {
        this.client = client;
    }

    /**
     * Create a new action
     *
     * @param options the options to apply
     */
    public ActionPut create(final ActionOptions options) {
        return doUpdate(options.overwrite(false));
    }

    /**
     * Update an existing action, or create an action if it does not exist
     * @param options the options to apply
     */
    public ActionPut update(final ActionOptions options) {
        return doUpdate(options.overwrite(true));
    }

    /**
     * Update an existing action, or create an action if it does not exist
     * @param options the options to apply
     */
    private ActionPut doUpdate(final ActionOptions options) {
        ActionPut put = Utils.encodeFile(options.toActionPut());

        String namespace = options.name().getNamespace();
        if(namespace == null) {
            namespace = client.getConfiguration().getNamespace();
        }
        final ActionPut result = new ActionsApi(client.getClient())
                                     .updateAction(namespace, options.name().getEntityName(), put, options.overwrite());

        final String code = result.getExec().getCode();
        final int length = code.length();
        if (length > 1000) {
            result.getExec().code(code.substring(0, Math.min(1000, length)));
        }
        return result;
    }

    /**
     * Invoke an action
     *
     * @param options the options to apply
     * @return the invocation response
     */
    @SuppressWarnings("unchecked")
    public <T> T invoke(InvokeOptions options) {

        final Activation activation = new ActionsApi(client.getClient())
                                          .invokeAction(client.getConfiguration().getNamespace(), options.name(), options.parameters(),
                                              options.blocking(), false, options.timeout());
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
     *
     * @param name the name of the action to fetch
     * @param includeCode true if the code content of the action is to be included in the returned value
     */
    public Action get(final String name, final boolean includeCode) {
        return new ActionsApi(client.getClient())
                   .getActionByName(client.getConfiguration().getNamespace(), name, includeCode);

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
