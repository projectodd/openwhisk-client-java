package org.projectodd.openwhisk

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Properties

class Configuration private constructor(
        var host: String = "localhost",
        var port: Int = DEFAULT_PORT,
        var auth: String? = null,
        var namespace: String = DEFAULT_NAMESPACE,
        var actionPackage: String = "",
        var isDebugging: Boolean = false,
        var isInsecure: Boolean = false,
        var timeout: Int = DEFAULT_TIMEOUT) {

    /**
     * Creates a new builder based on the current Configuration
     * @return the new [Builder]
     */
    fun from(): Builder {
        return Builder(this)
    }

    class Builder private constructor() {
        companion object {
            fun builder(): Builder {
                return Builder()
            }
        }

        private var host = DEFAULT_HOST
        private var debugging: Boolean = false
        private var insecure: Boolean = false
        private var port = DEFAULT_PORT
        private var timeout = DEFAULT_TIMEOUT
        private var auth: String? = null
        private var namespace = DEFAULT_NAMESPACE
        private var actionPackage = DEFAULT_ACTION_PACKAGE

        constructor(configuration: Configuration) : this() {
            host = configuration.host
            port = configuration.port
            auth = configuration.auth
            namespace = configuration.namespace
            actionPackage = configuration.actionPackage
            insecure = configuration.isInsecure
        }

        fun host(host: String): Builder {
            this.host = host
            return this
        }

        fun debugging(debugging: Boolean): Builder {
            this.debugging = debugging
            return this
        }

        fun insecure(insecure: Boolean): Builder {
            this.insecure = insecure
            return this
        }

        fun port(port: Int): Builder {
            this.port = port
            return this
        }

        fun timeout(timeout: Int): Builder {
            this.timeout = timeout
            return this
        }

        fun auth(auth: String): Builder {
            this.auth = auth
            return this
        }

        fun namespace(namespace: String): Builder {
            this.namespace = namespace
            return this
        }

        fun actionPackage(actionPackage: String): Builder {
            this.actionPackage = actionPackage
            return this
        }

        fun build(): Configuration {
            return Configuration(host, port, auth, namespace, actionPackage, debugging, insecure, timeout)
        }
    }

    companion object {
        private val API_HOST = "APIHOST"
        private val DEFAULT_HOST = "localhost"
        private val API_PORT = "APIPORT"
        private val DEFAULT_PORT = 443
        private val DEFAULT_TIMEOUT = 30000
        private val AUTH = "AUTH"
        private val DEFAULT_NAMESPACE = "_"
        private val DEFAULT_PROPS = ".wskprops"
        private val DEFAULT_PROPS_FILE = File(System.getProperty("user.home"), DEFAULT_PROPS)
        private val DEFAULT_ACTION_PACKAGE = ""

        @JvmOverloads
        fun load(file: File = DEFAULT_PROPS_FILE): Configuration {
            val configuration = Configuration()
            if (file.exists()) {
                val properties = Properties()
                try {
                    properties.load(FileInputStream(DEFAULT_PROPS_FILE))
                } catch (e: IOException) {
                    throw ConfigurationException("Failed to read configuration file", e)
                }

                configuration.host = read(properties, API_HOST, DEFAULT_HOST)
                configuration.port = Integer.parseInt(read(properties, API_PORT, DEFAULT_PORT))
                configuration.auth = read(properties, AUTH, null)
            }

            return configuration
        }

        private fun read(properties: Properties, name: String, defaultValue: Any?): String {
            return properties.getProperty(name, defaultValue?.toString())
        }

    }
}
