package org.projectodd.openwhisk

import org.projectodd.openwhisk.model.KeyValue

/**
 * Represents the options to apply to function invocations.
 */
class InvokeOptions(private val name: String) {
    private var blocking: Boolean = false
    private var results: Boolean = false
    private var local: Boolean = false
    private var timeout = 30000
    private val params = KeyValue()

    fun name(): String {
        return name
    }

    fun blocking(): Boolean {
        return blocking
    }

    fun blocking(blocking: Boolean): InvokeOptions {
        this.blocking = blocking
        return this
    }

    fun local(): Boolean {
        return local
    }

    fun local(local: Boolean): InvokeOptions {
        this.local = local
        return this
    }

    fun results(): Boolean {
        return results
    }

    fun results(results: Boolean): InvokeOptions {
        this.results = results
        return this
    }

    fun parameter(key: String, value: Any): InvokeOptions {
        params[key] = value
        return this
    }

    fun parameters(values: Map<String, Any>): InvokeOptions {
        params.putAll(values)
        return this
    }

    internal fun parameters(): KeyValue {
        return params
    }

    fun timeout(): Int {
        return timeout
    }

    fun timeout(timeout: Int): InvokeOptions {
        this.timeout = timeout
        return this
    }
}
