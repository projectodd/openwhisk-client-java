package org.projectodd.openwhisk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.KeyValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import static java.lang.String.format;

class Utils {
    static byte[] readFile(final File jarFile) throws IOException {
        try (final FileInputStream inputStream = new FileInputStream(jarFile)) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[49152];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            return stream.toByteArray();
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

    static ActionPut encodeFile(final ActionPut actionPut) {
        final String path = actionPut.getExec().getCode();
        final File file = new File(path);
        if(!file.exists()) {
            throw new IllegalArgumentException(format("'%s' does not exist.  Please specify a valid path to a file.", path));
        }
        final String encoded;
        try {
            if (FilenameUtils.isExtension(path, new String[]{"zip", "jar"})) {
                encoded = Base64.getEncoder().encodeToString(readFile(file));
            } else {
                encoded = FileUtils.readFileToString(file, "UTF-8");
            }
        } catch (IOException e) {
            throw new ConfigurationException(e.getMessage(), e);
        }

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
