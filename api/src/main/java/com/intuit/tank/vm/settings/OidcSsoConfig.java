package com.intuit.tank.vm.settings;

import org.apache.commons.configuration2.HierarchicalConfiguration;

/**
 * OidcSsoConfig
 *
 * @author Shawn Park
 */
public class OidcSsoConfig {
    private static final String KEY_AUTHORIZATION_ENDPOINT = "authorization-endpoint";
    private static final String KEY_AUTHORIZATION_URL = "authorization-url";
    private static final String KEY_CLIENT_SECRET = "client-secret";
    private static final String KEY_CLIENT_ID = "client-id";
    private static final String KEY_ISSUER = "issuer";
    private static final String KEY_REDIRECT_URL = "redirect-url";
    private static final String KEY_TOKEN_ENDPOINT = "token-endpoint";

    private final HierarchicalConfiguration config;

    public OidcSsoConfig(HierarchicalConfiguration config) {
        this.config = config;
    }

    /**
     * @return Authorization Endpoint
     */
    public String getAuthorizationEndpoint() { return config.getString(KEY_AUTHORIZATION_ENDPOINT); }

    /**
     * @return Authorization Url
     */
    public String getAuthorizationUrl() { return config.getString(KEY_AUTHORIZATION_URL); }

    /**
     * @return Client ID
     */
    public String getClientId() { return config.getString(KEY_CLIENT_ID); }

    /**
     * @return Client Secret
     */
    public String getClientSecret() { return config.getString(KEY_CLIENT_SECRET); }

    /**
     * @return Hierarchical Config
     */
    public HierarchicalConfiguration getConfiguration() { return config; }

    /**
     * @return Issuer
     */
    public String getIssuer() { return config.getString(KEY_ISSUER); }

    /**
     * @return Redirect URL
     */
    public String getRedirectUrl() { return config.getString(KEY_REDIRECT_URL); }

    /**
     * @return Token Endpoint
     */
    public String getTokenEndpoint() { return config.getString(KEY_TOKEN_ENDPOINT); }
}
