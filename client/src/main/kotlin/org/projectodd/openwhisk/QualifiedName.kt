package org.projectodd.openwhisk

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Arrays

class QualifiedName {

    /**
     * @return the name of the namespace in qualifiedName without a leading '/
     */
    var namespace: String? = null
        private set
    /**
     * @return the entity name ([package/]entity) of qualifiedName without a leading '/'
     */
    var entityName: String? = null
        private set
    /**
     * @return the package name from qualifiedName without a leading '/'
     */
    var packageName: String? = null
        private set
    /**
     * @return the name of entity in qualifiedName without a leading '/'
     */
    var entity: String? = null
        private set

    companion object {
        private val LOG = LoggerFactory.getLogger(QualifiedName::class.java)

        /**
         * Initializes and constructs a (possibly fully qualified) QualifiedName.
         *
         *
         * NOTE: If the given qualified name is None, then this is a default qualified
         * name and it is resolved from properties.
         * NOTE: If the namespace is missing from the qualified name, the namespace
         * is also resolved from the property file.
         *
         *
         * Examples:
         * foo => qualifiedName {namespace: "_", entityName: foo}
         * pkg/foo => qualifiedName {namespace: "_", entityName: pkg/foo}
         * /ns/foo => qualifiedName {namespace: ns, entityName: foo}
         * /ns/pkg/foo => qualifiedName {namespace: ns, entityName: pkg/foo}
         */
        fun qualifiedName(name: String?): QualifiedName {
            var name = name
            val qualifiedName = QualifiedName()
            if (name == null) {
                qualifiedNameNotSpecifiedErr()
            }
            // If name has a preceding delimiter (/), or if it has two delimiters with a
            // leading non-empty string, then it contains a namespace. Otherwise the name
            // does not specify a namespace, so default the namespace to the namespace
            // value set in the properties file; if that is not set, use "_"
            name = addLeadSlash(name)
            val parts = name!!.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (name.startsWith("/")) {
                qualifiedName.namespace = parts[1]

                if (parts.size < 2 || parts.size > 4) {
                    qualifiedNameNotSpecifiedErr()
                }

                for (i in 1 until parts.size) {
                    if (parts[i].length == 0 || parts[i] == ".") {
                        qualifiedNameNotSpecifiedErr()
                    }
                }

                qualifiedName.entityName = Arrays.asList(*parts).subList(2, parts.size).joinToString("/")
                if (parts.size == 4) {
                    qualifiedName.packageName = parts[2]
                }
                qualifiedName.entity = parts[parts.size - 1]
            } else {
                if (name.length == 0 || name == ".") {
                    qualifiedNameNotSpecifiedErr()
                }

                qualifiedName.entity = parts[parts.size - 1]
                if (parts.size == 2) {
                    qualifiedName.packageName = parts[0]
                }
                qualifiedName.entityName = name
                qualifiedName.namespace = "_"
            }

            LOG.debug("Qualified pkg+entity (EntityName): %s", qualifiedName.entityName)
            LOG.debug("Qualified namespace: %s", qualifiedName.namespace)
            LOG.debug("Qualified package: %s", qualifiedName.packageName)
            LOG.debug("Qualified entity: %s", qualifiedName.entity)

            return qualifiedName
        }

        private fun qualifiedNameNotSpecifiedErr() {
            throw ConfigurationException("A valid qualified name must be specified.")
        }

        /**
         * addLeadSlash(name) returns a (possibly fully qualified) resource name,
         * inserting a leading '/' if it is of 3 parts (namespace/package/action)
         * and lacking the leading '/'.
         */
        private fun addLeadSlash(name: String?): String? {
            if (name == null) {
                return null
            }

            val parts = name.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            return if (parts.size == 3 && parts[0] != "") {
                "/$name"
            } else {
                name
            }
        }
    }
}
