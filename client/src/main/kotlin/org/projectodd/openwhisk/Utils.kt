package org.projectodd.openwhisk

import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.projectodd.openwhisk.model.ActionExec
import org.projectodd.openwhisk.model.ActionPut
import org.projectodd.openwhisk.model.KeyValue

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Base64

import java.lang.String.format

internal object Utils {
    @Throws(IOException::class)
    fun readFile(jarFile: File): ByteArray {
        FileInputStream(jarFile).use { inputStream ->
            val stream = ByteArrayOutputStream()
            val buffer = ByteArray(49152)
            var read: Int
            do {
                read = inputStream.read(buffer)
                if(read != -1) stream.write(buffer, 0, read)
            } while (read != -1)
            return stream.toByteArray()
        }
    }

    fun copy(actionPut: ActionPut): ActionPut {
        return ActionPut()
                .version(actionPut.version)
                .publish(actionPut.isPublish)
                .exec(copy(actionPut.exec))
                .annotations(actionPut.annotations)
                .parameters(actionPut.parameters)
                .limits(actionPut.limits)

    }

    private fun copy(exec: ActionExec): ActionExec {
        return ActionExec()
                .code(exec.code)
                .kind(exec.kind)
                .image(exec.image)
                .init(exec.init)
                .main(exec.main)
    }

    fun encodeFile(actionPut: ActionPut): ActionPut {
        val path = actionPut.exec.code
        val file = File(path)
        if (!file.exists()) {
            throw IllegalArgumentException(format("'%s' does not exist.  Please specify a valid path to a file.", path))
        }
        val encoded: String
        try {
            if (FilenameUtils.isExtension(path, arrayOf("zip", "jar"))) {
                encoded = Base64.getEncoder().encodeToString(readFile(file))
            } else {
                encoded = FileUtils.readFileToString(file, "UTF-8")
            }
        } catch (e: IOException) {
            throw ConfigurationException(e.message, e)
        }

        val newPut = copy(actionPut)
        newPut.exec.code(encoded)
        return newPut
    }

}
