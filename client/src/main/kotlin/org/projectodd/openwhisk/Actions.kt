package org.projectodd.openwhisk

import org.projectodd.openwhisk.actors.LocalFunction
import org.projectodd.openwhisk.api.ActionsApi
import org.projectodd.openwhisk.invoker.ApiException
import org.projectodd.openwhisk.model.Action
import org.projectodd.openwhisk.model.ActionPut

class Actions internal constructor(private val client: OWskClient) {

    /**
     * Create a new action
     *
     * @param options the options to apply
     */
    fun create(options: ActionOptions): ActionPut {
        return doUpdate(options.overwrite(false))
    }

    /**
     * Update an existing action, or create an action if it does not exist
     *
     * @param options the options to apply
     */
    fun update(options: ActionOptions): ActionPut {
        return doUpdate(options.overwrite(true))
    }

    /**
     * Update an existing action, or create an action if it does not exist
     *
     * @param options the options to apply
     */
    private fun doUpdate(options: ActionOptions): ActionPut {
        val put = Utils.encodeFile(options.toActionPut())

        var namespace = options.name().namespace
        if (namespace == null) {
            namespace = client.configuration!!.namespace
        }
        val result = ActionsApi(client.client)
                .updateAction(namespace, options.name().entityName, put, options.overwrite())

        val code = result.exec.code
        val length = code.length
        if (length > 1000) {
            result.exec.code(code.substring(0, Math.min(1000, length)))
        }
        return result
    }

    /**
     * Invoke an action
     *
     * @param options the options to apply
     * @return the invocation response
     */
    operator fun <T> invoke(options: InvokeOptions): T {

        val activation = if (options.local()) {
            LocalFunction.invoke(options.name(), options.parameters(), options)
        } else {
            ActionsApi(client.client)
                    .invokeAction(client.configuration!!.namespace, options.name(), options.parameters(),
                            options.blocking(), false, options.timeout())
        }

        return when {
            options.results() -> activation.response.result
            options.blocking() -> activation
            else -> activation.activationId
        }  as T
    }

    /**
     * Get an action
     *
     * @param name        the name of the action to fetch
     * @param includeCode true if the code content of the action is to be included in the returned value
     */
    operator fun get(name: String, includeCode: Boolean): Action {
        return ActionsApi(client.client)
                .getActionByName(client.configuration!!.namespace, name, includeCode)

    }

    /**
     * Delete action
     *
     * @param name the name of the action to delete
     * @throws ApiException if the named action does not exist
     */
    fun delete(name: String) {
        ActionsApi(client.client)
                .deleteAction(client.configuration!!.namespace, name)
    }

    /**
     * List all actions in a namespace or actions contained in a package
     */
    fun list() {
        throw UnsupportedOperationException("method not yet implemented")
    }

}
