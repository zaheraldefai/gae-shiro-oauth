// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        AuthBase.java  (08-Oct-2012)
// Author:      tim
//
// Copyright in the whole and every part of this source file belongs to
// Tim Niblett (the Author) and may not be used, sold, licenced, 
// transferred, copied or reproduced in whole or in part in 
// any manner or form or in or on any media to any person other than 
// in accordance with the terms of The Author's agreement
// or otherwise without the prior written consent of The Author.  All
// information contained in this source file is confidential information
// belonging to The Author and as such may not be disclosed other
// than in accordance with the terms of The Author's agreement, or
// otherwise, without the prior written consent of The Author.  As
// confidential information this source file must be kept fully and
// effectively secure at all times.
//
// changes by zaher
//

package de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.provider;

import de.zaheraldefai.samples.appengine_shiro_oauth.security.UserAuthType;
import de.zaheraldefai.samples.appengine_shiro_oauth.security.oauth.OAuthInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Properties;
import java.util.logging.Logger;

public abstract class BaseOAuthProviderInfo {
	
	static final Logger LOG = Logger.getLogger(BaseOAuthProviderInfo.class.getName());

    private String state;
	
    protected final String apiKey;
    protected final String apiSecret;
    protected final String callbackUrl;
    
    BaseOAuthProviderInfo(String apiKey, String apiSecret, String callbackUrl) {
    	
    	this.apiKey = apiKey;
    	this.apiSecret = apiSecret;
    	this.callbackUrl = callbackUrl;
    	
    	// TODO what is this state!
        state = "random string";
    }
    
    /**
     * Which type of authorization is being used?  Each one is handled somewhat differently.
     * @return  The enum type
     */
    public abstract UserAuthType getUserAuthType();

    /**
     * The URL to call on login, to get a code.
     * @param callbackUri  Where to go afterwards.
     * @return
     */
    public abstract String loginURL();
    /**
     * The URL to call on re-authentication, to get a code.
     * @param callbackUri  Where to go afterwards.
     * @return
     */
    public abstract String reAuthenticateURL();

    /**
     * Verify the User and give Information about the user. 
     * At the moment its pretty-much just the email address.
     * 
     * @param code The auth token / authorization code
     * @return The info.
     */
    public abstract OAuthInfo getUserInfoWithAuthorizationCode(String code);
    
    /**
     * Verify the User and give Information about the user. 
     * At the moment its pretty-much just the email address.
     * 
     * @param access_token The auth access token
     * @return The info.
     */
    public abstract OAuthInfo getUserInfoWithAccessToken(String access_token);

    /**
     * Revoked a token whilst in a web session
     * @param token The token to revoke
     * @param request  Current request
     * @param response Current response
     * @param redirectURL Where you'd like to be redirected to (put just the URLPath!)
     */
    public abstract void revokeToken(String token, HttpServletRequest request, HttpServletResponse response, String redirectURL) throws IOException;

    
    
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    protected void loadProperties(Properties props, String resourceName) {
        try {
            props.load(getClass().getResourceAsStream(resourceName));
        } catch (IOException e) {
            LOG.severe("Can't load resource "+resourceName + ": " + e.getMessage());
        }
    }

    protected String makeAbsolute(String uri, String host) {
        return uri.startsWith("/") ? host + uri : uri;
    }

    protected String errorString(JSONObject obj) {
        if (obj.has("error")) {
            try {
                JSONObject errObj = obj.getJSONObject("error");
                String message = errObj.getString("message");
                return (message == null) ? "unknown JSON error, no message field found" : message;
            } catch (JSONException e) {
                return "Unknown error, JSON won't parse: " + obj.toString();
            }
        } else {
            return null;
        }
    }
}