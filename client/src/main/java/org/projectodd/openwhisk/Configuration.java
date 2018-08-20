package org.projectodd.openwhisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private static final String API_HOST = "APIHOST";
    private static final String DEFAULT_HOST = "localhost";
    private static final String API_PORT = "APIPORT";
    private static final int DEFAULT_PORT = 443;
    private static final int DEFAULT_TIMEOUT = 30000;
    private static final String AUTH = "AUTH";
    private static final String DEFAULT_NAMESPACE = "_";
    private static final String DEFAULT_PROPS = ".wskprops";
    private static final File DEFAULT_PROPS_FILE = new File(System.getProperty("user.home"), DEFAULT_PROPS);
    private static final String DEFAULT_ACTION_PACKAGE = "";

    private String host = "localhost";
    private int port = DEFAULT_PORT;
    private String auth;
    private String namespace = DEFAULT_NAMESPACE;
    private String actionPackage = "";
    private boolean debugging = false;
    private boolean insecure = false;
    private int timeout = DEFAULT_TIMEOUT;

    private Configuration() {
    }

    private Configuration(final String host, final int port, final String auth, final String namespace, final String actionPackage,
                          boolean debugging, boolean insecure, final int timeout) {
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.namespace = namespace;
        this.actionPackage = actionPackage;
        this.debugging = debugging;
        this.insecure = insecure;
        this.timeout = timeout;
    }

    public String getHost() {
        return host;
    }

    public boolean isDebugging() {
        return debugging;
    }

    public boolean isInsecure() {
        return insecure;
    }

    public int getPort() {
        return port;
    }

    public String getAuth() {
        return auth;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getActionPackage() {
        return actionPackage;
    }

    public static Configuration load() {
        return load(DEFAULT_PROPS_FILE);
    }

    public static Configuration load(final File file) {
        final Configuration configuration = new Configuration();
        if (file.exists()) {
            final Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(DEFAULT_PROPS_FILE));
            } catch (IOException e) {
                throw new ConfigurationException("Failed to read configuration file", e);
            }
            configuration.host = read(properties, API_HOST, DEFAULT_HOST);
            configuration.port = Integer.parseInt(read(properties, API_PORT, DEFAULT_PORT));
            configuration.auth = read(properties, AUTH, null);
        }

        return configuration;
    }

    private static String read(final Properties properties, final String name, final Object defaultValue) {
        return properties.getProperty(name, defaultValue == null ? null : defaultValue.toString());
    }

    /**
     * Creates a new builder based on the current Configuration
     * @return the new {@link Builder}
     */
    public Builder from() {
        return new Builder(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String host = DEFAULT_HOST;
        private boolean debugging;
        private boolean insecure;
        private int port = DEFAULT_PORT;
        private int timeout = DEFAULT_TIMEOUT;
        private String auth;
        private String namespace = DEFAULT_NAMESPACE;
        private String actionPackage = DEFAULT_ACTION_PACKAGE;

        public Builder() {
        }

        public Builder(final Configuration configuration) {
            host = configuration.host;
            port = configuration.port;
            auth = configuration.auth;
            namespace = configuration.namespace;
            actionPackage = configuration.actionPackage;
            insecure = configuration.insecure;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder debugging(boolean debugging) {
            this.debugging = debugging;
            return this;
        }

        public Builder insecure(boolean insecure) {
            this.insecure = insecure;
            return this;
        }

        public Builder port(int port) {
            this.port = port;
            return this;
        }
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder auth(String auth) {
            this.auth = auth;
            return this;
        }

        public Builder namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder actionPackage(String actionPackage) {
            this.actionPackage = actionPackage;
            return this;
        }

        public Configuration build() {
            return new Configuration(host, port, auth, namespace, actionPackage, debugging, insecure, timeout);
        }
    }
}
