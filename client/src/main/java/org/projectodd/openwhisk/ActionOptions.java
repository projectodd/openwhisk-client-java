package org.projectodd.openwhisk;

import org.projectodd.openwhisk.model.ActionExec;
import org.projectodd.openwhisk.model.ActionExec.KindEnum;
import org.projectodd.openwhisk.model.ActionPut;
import org.projectodd.openwhisk.model.KeyValue;

public class ActionOptions {
    private final String name;
    private ActionPut actionPut = new ActionPut()
                                      .exec(new ActionExec());
    private boolean overwrite;

    public ActionOptions(final String name) {
        this.name = name;
    }

    public String name() {
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
            final KeyValue keyValue = new KeyValue();
            keyValue.put("web-export", true);
            actionPut.addAnnotationsItem(keyValue);
        }
        return this;

    }

    public ActionOptions webSecure(final boolean secure) {
        if (secure) {
            final KeyValue keyValue = new KeyValue();
            keyValue.put("web-export", true);
            actionPut.addAnnotationsItem(keyValue);
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
}
