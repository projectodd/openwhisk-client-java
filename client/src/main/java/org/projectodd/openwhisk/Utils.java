package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.KeyValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static java.lang.String.format;

class Utils {
    static byte[] readFile(final File jarFile) {
        try (final FileInputStream inputStream = new FileInputStream(jarFile)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[49152];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            return stream.toByteArray();
        } catch (IOException e) {
            throw new ConfigurationException("Failed to read jar file: " + e.getMessage(), e);
        }
    }

    static ActionPut copy(final ActionPut actionPut) {
        return new ActionPut()
                   .version(actionPut.getVersion())
                   .publish(actionPut.isPublish())
                   .exec(copy(actionPut.getExec()))
                   .annotations(actionPut.getAnnotations())
                   .parameters(actionPut.getParameters())
                   .limits(actionPut.getLimits());

    }

    private static ActionExec copy(final ActionExec exec) {
        return new ActionExec()
            .code(exec.getCode())
            .kind(exec.getKind())
            .image(exec.getImage())
            .init(exec.getInit())
            .main(exec.getMain());
    }

    static ActionPut updatePut(final ActionPut actionPut) {
        final String jarPath = actionPut.getExec().getCode();
        final File jarFile = new File(jarPath);
        if(!jarFile.exists()) {
            throw new IllegalArgumentException(format("'%s' does not exist.  Please specify a valid path to a jar file.", jarPath));
        }
        final String encoded = Base64.getEncoder().encodeToString(readFile(jarFile));
        final ActionPut newPut = copy(actionPut);
        newPut.getExec().code(encoded);
        return newPut;
    }

    static KeyValue keyValue(final Map<String, String> map) {
        final KeyValue keyValue = new KeyValue();
        keyValue.putAll(map);
        return keyValue;
    }
}
