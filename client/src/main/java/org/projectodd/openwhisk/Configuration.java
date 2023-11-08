package org.projectodd.openwhisk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private boolean useOauth;
    private String oauthTokenUrl;
    private Map<String, String> oauthTokenRequestParameters;
    private Map<String, String> oauthTokenRequestHeaders;
    private String namespace = DEFAULT_NAMESPACE;
    private String actionPackage = "";
    private boolean debugging = false;
    private boolean insecure = false;
    private int timeout = DEFAULT_TIMEOUT;

    private Configuration() {
    }

    private Configuration(final String host, final int port, final String auth, final boolean useOauth,
            final String oauthTokenUrl,
            final Map<String, String> oauthTokenRequestParameters,
            final Map<String, String> oauthTokenRequestHeaders,
            final String namespace, final String actionPackage,
                          boolean debugging, boolean insecure, final int timeout) {
        this.host = host;
        this.port = port;
        this.auth = auth;
        this.useOauth = useOauth;
        this.oauthTokenUrl = oauthTokenUrl;
        this.oauthTokenRequestParameters = oauthTokenRequestParameters;
        this.oauthTokenRequestHeaders = oauthTokenRequestHeaders;
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

    public boolean useOauth() {
        return useOauth;
    }

    public String getOauthTokenUrl() {
        return oauthTokenUrl;
    }

    public Map<String, String> getOauthTokenRequestParameters() {
        return new HashMap<>(oauthTokenRequestParameters);
    }

    public Map<String, String> getOauthTokenRequestHeaders() {
        return new HashMap<>(oauthTokenRequestHeaders);
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
        private boolean useOauth;
        private String oauthTokenUrl;
        private Map<String, String> oauthTokenRequestParameters;
        private Map<String, String> oauthTokenRequestHeaders;
        private String namespace = DEFAULT_NAMESPACE;
        private String actionPackage = DEFAULT_ACTION_PACKAGE;

        public Builder() {
        }

        public Builder(final Configuration configuration) {
            host = configuration.host;
            port = configuration.port;
            auth = configuration.auth;
            useOauth = configuration.useOauth;
            oauthTokenUrl = configuration.oauthTokenUrl;
            oauthTokenRequestParameters = configuration.oauthTokenRequestParameters;
            oauthTokenRequestHeaders = configuration.oauthTokenRequestHeaders;
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

        public Builder useOauth(boolean useOauth) {
            this.useOauth = useOauth;
            return this;
        }

        public Builder oauthTokenUrl(String oauthTokenUrl) {
            this.oauthTokenUrl = oauthTokenUrl;
            return this;
        }

        public Builder oauthTokenRequestParameters(
                Map<String, String> requestParameters) {
            this.oauthTokenRequestParameters = new HashMap<>(
                    requestParameters);
            return this;
        }

        public Builder oauthTokenRequestHeaders(
                Map<String, String> requestHeaders) {
            this.oauthTokenRequestHeaders = new HashMap<>(
                    requestHeaders);
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
            if (useOauth && (oauthTokenUrl == null
                    || oauthTokenUrl.isEmpty())) {
                throw new IllegalStateException(
                        "No URL provided to request Oauth token");
            }
            return new Configuration(host, port, auth, useOauth, oauthTokenUrl,
                    oauthTokenRequestParameters, oauthTokenRequestHeaders,
                    namespace, actionPackage, debugging, insecure, timeout);
        }
    }
}
