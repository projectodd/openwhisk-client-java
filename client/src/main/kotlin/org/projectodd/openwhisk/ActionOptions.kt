package org.projectodd.openwhisk

import org.projectodd.openwhisk.model.ActionExec
import org.projectodd.openwhisk.model.ActionExec.KindEnum
import org.projectodd.openwhisk.model.ActionPut
import org.projectodd.openwhisk.model.KeyValue

import java.security.SecureRandom

class ActionOptions(name: String) {
    private val name: QualifiedName
    private val actionPut = ActionPut()
            .exec(ActionExec())
    private var overwrite: Boolean = false

    init {
        this.name = QualifiedName.qualifiedName(name)
    }

    fun name(): QualifiedName {
        return name
    }

    internal fun toActionPut(): ActionPut {
        return actionPut

    }

    fun code(code: String): ActionOptions {
        actionPut.exec.code(code)
        return this
    }

    fun main(main: String): ActionOptions {
        actionPut.exec.main(main)
        return this
    }

    fun kind(kind: KindEnum): ActionOptions {
        actionPut.exec.kind(kind)
        return this
    }

    fun web(webEnabled: Boolean): ActionOptions {
        if (webEnabled) {
            putAnnotation("web-export", "true")
        }
        return this
    }


    fun webSecure(secure: Boolean): ActionOptions {
        if (secure) {
            putAnnotation("require-whisk-auth", genWebActionSecureKey())
        }
        return this

    }

    fun overwrite(overwrite: Boolean): ActionOptions {
        this.overwrite = overwrite
        return this
    }

    fun overwrite(): Boolean {
        return overwrite
    }

    private fun genWebActionSecureKey(): Long {
        val random = SecureRandom()
        random.setSeed(System.currentTimeMillis())

        var key = random.nextLong()
        if (key < 0) {
            key += java.lang.Long.MAX_VALUE
        }
        return key
    }

    private fun putAnnotation(key: String, value: Any) {
        val annotations = KeyValue()
        annotations["key"] = key
        annotations["value"] = value
        actionPut.addAnnotationsItem(annotations)
    }
}
