package org.projectodd.openwhisk.actors

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.projectodd.openwhisk.InvokeOptions
import org.projectodd.openwhisk.model.Activation
import org.projectodd.openwhisk.model.ActivationResponse
import java.lang.reflect.Method

object LocalFunction {
    val functions = mutableMapOf<String, Method>()

    @JvmStatic
    fun register(name: String, klass: Class<*>) {
        val method = klass.getMethod("main")
        functions.put(name, method)
    }

    @JvmStatic
    fun invoke(name: String, params: Map<String, Any>, options: InvokeOptions): Activation {
        val job = async {
            functions[name]?.invoke(params)
        }

        val activation = Activation()
        activation.response = ActivationResponse()
        activation.activationId = job.hashCode().toString()
        if(!options.blocking()) {
            TODO("non-blocking execution not yet supported")
        } else {
            activation.response.result = runBlocking { job.await() }
        }

        return activation
    }
}