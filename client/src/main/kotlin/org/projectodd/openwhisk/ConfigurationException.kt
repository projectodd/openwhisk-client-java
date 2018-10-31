package org.projectodd.openwhisk

internal class ConfigurationException : RuntimeException {
    constructor(message: String) : super(message)

    constructor(message: String?, cause: Throwable) : super(message, cause)
}
