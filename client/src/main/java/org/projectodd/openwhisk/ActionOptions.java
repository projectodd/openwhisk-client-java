package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.KeyValue;

import java.security.SecureRandom;

public class ActionOptions {
    private final QualifiedName name;
    private ActionPut actionPut = new ActionPut()
                                      .exec(new ActionExec());
    private boolean overwrite;

    public ActionOptions(final String name) {
        this.name = QualifiedName.qualifiedName(name);
    }

    public QualifiedName name() {
        return name;
    }

    ActionPut toActionPut() {
        return actionPut;

    }

    public ActionOptions code(final String code) {
        actionPut.getExec().code(code);
        return this;
    }

    public ActionOptions main(final String main) {
        actionPut.getExec().main(main);
        return this;
    }

    public ActionOptions kind(final KindEnum kind) {
        actionPut.getExec().kind(kind);
        return this;
    }

    public ActionOptions web(final boolean webEnabled) {
        if (webEnabled) {
            putAnnotation("web-export", "true");
        }
        return this;
    }


    public ActionOptions webSecure(final boolean secure) {
        if (secure) {
            putAnnotation("require-whisk-auth", genWebActionSecureKey());
        }
        return this;

    }

    public ActionOptions overwrite(final boolean overwrite) {
        this.overwrite = overwrite;
        return this;
    }

    public boolean overwrite() {
        return overwrite;
    }

    public ActionOptions image(final String image) {
        actionPut.getExec().image(image);
        return this;
    }

    private long genWebActionSecureKey() {
        final SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis());

        long key = random.nextLong();
        if (key < 0) {
            key += Long.MAX_VALUE;
        }
        return key;
    }

    private void putAnnotation(final String key, final Object value) {
        final KeyValue annotations = new KeyValue();
        annotations.put("key", key);
        annotations.put("value", value);
        actionPut.addAnnotationsItem(annotations);
    }
}
