package org.projectodd.openwhisk;

import org.projectodd.openwhisk.api.ActivationsApi;
import org.projectodd.openwhisk.invoker.ApiException;

public class Activations {

    private final ActivationsApi activationsApi;
    private final String namespace;

    Activations(OWskClient client) {
        if (client == null) {
            throw new IllegalArgumentException(
                    "Null passed as client to "
                            + Activations.class.getCanonicalName()
                            + " constructor");
        }
        if (client.getClient() == null) {
            throw new IllegalArgumentException(
                    "Null apiClient inside "
                            + OWskClient.class.getCanonicalName());
        }
        if (client.getConfiguration() == null) {
            throw new IllegalArgumentException(
                    "Null configuration inside "
                            + OWskClient.class.getCanonicalName());
        }
        if (client.getConfiguration().getNamespace() == null
                || client.getConfiguration().getNamespace()
                        .isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing namespace inside client configuration");
        }
        this.activationsApi = new ActivationsApi(client.getClient());
        this.namespace = client.getConfiguration().getNamespace();
    }

    /**
     * Get an activation's result
     */
    public Object getActivationResult(String activationId)
            throws ApiException {
        if (activationId == null
                || activationId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing activation id");
        }
        return activationsApi
                .namespacesNamespaceActivationsActivationidResultGet(
                        namespace, activationId).getResult();
    }
}
