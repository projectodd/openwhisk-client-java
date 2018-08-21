package org.projectodd.openwhisk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class QualifiedName {
    private static final Logger LOG = LoggerFactory.getLogger(QualifiedName.class);

    private String namespace;
    private String entityName;
    private String packageName;
    private String entity;

    /**
     * @return the name of the namespace in qualifiedName without a leading '/
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return the entity name ([package/]entity) of qualifiedName without a leading '/'
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @return the package name from qualifiedName without a leading '/'
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @return the name of entity in qualifiedName without a leading '/'
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Initializes and constructs a (possibly fully qualified) QualifiedName.
     * <p>
     * NOTE: If the given qualified name is None, then this is a default qualified
     * name and it is resolved from properties.
     * NOTE: If the namespace is missing from the qualified name, the namespace
     * is also resolved from the property file.
     * <p>
     * Examples:
     * foo => qualifiedName {namespace: "_", entityName: foo}
     * pkg/foo => qualifiedName {namespace: "_", entityName: pkg/foo}
     * /ns/foo => qualifiedName {namespace: ns, entityName: foo}
     * /ns/pkg/foo => qualifiedName {namespace: ns, entityName: pkg/foo}
     */
    public static QualifiedName qualifiedName(String name) {
        final QualifiedName qualifiedName = new QualifiedName();
        if (name == null) {
            qualifiedNameNotSpecifiedErr();
        }
        // If name has a preceding delimiter (/), or if it has two delimiters with a
        // leading non-empty string, then it contains a namespace. Otherwise the name
        // does not specify a namespace, so default the namespace to the namespace
        // value set in the properties file; if that is not set, use "_"
        name = addLeadSlash(name);
        final String[] parts = name.split("/");
        if (name.startsWith("/")) {
            qualifiedName.namespace = parts[1];

            if (parts.length < 2 || parts.length > 4) {
                qualifiedNameNotSpecifiedErr();
            }

            for (int i = 1; i < parts.length; i++) {
                if (parts[i].length() == 0 || parts[i].equals(".")) {
                    qualifiedNameNotSpecifiedErr();
                }
            }

            qualifiedName.entityName = String.join("/", Arrays.asList(parts).subList(2, parts.length));
            if (parts.length == 4) {
                qualifiedName.packageName = parts[2];
            }
            qualifiedName.entity = parts[parts.length - 1];
        } else {
            if (name.length() == 0 || name.equals(".")) {
                qualifiedNameNotSpecifiedErr();
            }

            qualifiedName.entity = parts[parts.length - 1];
            if (parts.length == 2) {
                qualifiedName.packageName = parts[0];
            }
            qualifiedName.entityName = name;
            qualifiedName.namespace = null;
        }

        LOG.debug("Qualified pkg+entity (EntityName): %s", qualifiedName.getEntityName());
        LOG.debug("Qualified namespace: %s", qualifiedName.getNamespace());
        LOG.debug("Qualified package: %s", qualifiedName.getPackageName());
        LOG.debug("Qualified entity: %s", qualifiedName.getEntity());

        return qualifiedName;
    }

    private static void qualifiedNameNotSpecifiedErr() {
        throw new ConfigurationException("A valid qualified name must be specified.");
    }

    /**
     * addLeadSlash(name) returns a (possibly fully qualified) resource name,
     * inserting a leading '/' if it is of 3 parts (namespace/package/action)
     * and lacking the leading '/'.
     */
    private static String addLeadSlash(final String name) {
        if (name == null) {
            return null;
        }

        final String[] parts = name.split("/");
        if (parts.length == 3 && !parts[0].equals("")) {
            return "/" + name;
        } else {
            return name;
        }
    }
}
