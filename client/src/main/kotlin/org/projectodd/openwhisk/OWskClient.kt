package org.projectodd.openwhisk

import org.projectodd.openwhisk.invoker.ApiClient
import org.projectodd.openwhisk.invoker.JSON

import java.lang.String.format
import java.nio.charset.StandardCharsets.ISO_8859_1
import okio.ByteString.encodeString

class OWskClient {
    internal var client: ApiClient? = null
        private set
    var configuration: Configuration = Configuration.load()
        private set

    init {
        configure(configuration)
    }

    fun configure(configuration: Configuration) {
        this.configuration = configuration
        client = ApiClient()
        client!!.basePath = format("https://%s:%s/api/v1", configuration.host, configuration.port)

        val json = client!!.json
        json.gson = json.gson.newBuilder().disableHtmlEscaping().create()

        client!!.isVerifyingSsl = !configuration.isInsecure
        if (configuration.auth != null) {
            client!!.addDefaultHeader("Authorization", "Basic " + encodeString(configuration.auth!!, ISO_8859_1).base64())
        }
        client!!.setUserAgent("Incubating Apache OpenWhisk Java client")
        client!!.isDebugging = configuration.isDebugging
    }

    fun actions(): Actions {
        return Actions(this)
    }
}
