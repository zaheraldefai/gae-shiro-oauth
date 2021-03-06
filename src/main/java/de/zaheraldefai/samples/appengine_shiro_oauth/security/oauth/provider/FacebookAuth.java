// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        FacebookAuth.java  (05-Oct-2012)
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

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.scribe.utils.OAuthEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


public class FacebookAuth extends BaseOAuthProviderInfo {
    static final Logger LOG = Logger.getLogger(FacebookAuth.class.getName());

    private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/me";
    
    // TODO Why is that here?
    private static final Token EMPTY_TOKEN = null;
	
    

//    @Inject
//    public FacebookAuth(@Named("social.site") String site) {
//        LOG.info("prefix is " + site);
//        String prefix = "fb." + site;
//        Properties props = new Properties();
//        loadProperties(props, "/social.properties");
//        apiKey = props.getProperty(prefix + ".apiKey");
//        apiSecret = props.getProperty(prefix + ".apiSecret");
//        host = props.getProperty(prefix + ".host");
//    }
    
    public FacebookAuth(String apiKey, String apiSecret, String callbackUrl) {
    	super(apiKey, apiSecret, callbackUrl);
    }

    @Override
    public UserAuthType getUserAuthType() {
        return UserAuthType.FACEBOOK;
    }

    @Override
    public String loginURL() {
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(callbackUrl)
                                      .scope("email")
                                      .build();
        return service.getAuthorizationUrl(EMPTY_TOKEN);
    }

    @Override
    public String reAuthenticateURL() {
        return loginURL()+"&auth_type=reauthenticate";
    }

    @Override
    public OAuthInfo getUserInfoWithAuthorizationCode(String code) {
        JSONObject obj = getUserInfoJSONWithAuthorizationCode(code);
        return new OAuthInfo.Builder(UserAuthType.FACEBOOK)
                .errorString(errorString(obj))
                .email(obj.optString("email"))
                .token(obj.optString("access_token"))
                .build();
    }
    
    @Override
    public OAuthInfo getUserInfoWithAccessToken(String access_token) {
        JSONObject obj = getUserInfoJSONWithAccessToken(access_token);
        return new OAuthInfo.Builder(UserAuthType.FACEBOOK)
                .errorString(errorString(obj))
                .email(obj.optString("email"))
                .token(obj.optString("access_token"))
                .build();
    }

    /**
     * This method verify the User and give an JSON Object with all the information back
     * @param code The code Parameter from a success
     * @return
     */
    private JSONObject getUserInfoJSONWithAuthorizationCode(String code) {
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(callbackUrl)
                                      .scope("email")
                                      .build();
        Verifier verifier = new Verifier(code);
        Token accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        try {
            JSONObject obj =  new JSONObject(response.getBody());
            obj.put("access_token", accessToken.getToken());
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }
    
    /**
     * This method verify the User and give an JSON Object with all the information back
     * @param access_token The access_token Parameter from a success
     * @return
     */
    private JSONObject getUserInfoJSONWithAccessToken(String access_token) {
        OAuthService service = new ServiceBuilder()
                                      .provider(FacebookApi.class)
                                      .apiKey(apiKey)
                                      .apiSecret(apiSecret)
                                      .callback(callbackUrl)
                                      .scope("email")
                                      .build();
        Token accessToken = new Token(access_token, apiSecret); 
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        Response response = request.send();
        try {
            JSONObject obj =  new JSONObject(response.getBody());
            obj.put("access_token", accessToken.getToken());
            return obj;
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    // I'm not sure, if I need this
    public void revokeToken(String token, HttpServletRequest request, HttpServletResponse response,
                            String redirectURL)  throws IOException {
        String redirectHome = makeRoot(request.getRequestURL().toString(), redirectURL);

        String url = logoutUrl(redirectHome, token);
        response.sendRedirect(response.encodeRedirectURL(url));
    }

    // I'm not sure, if I need this
    public static String logoutUrl(String redirect, String accessToken) throws IOException {
        String redirectOK = OAuthEncoder.encode(redirect);
        return "https://www.facebook.com/logout.php?next="+redirectOK+"&access_token=" + accessToken;
    }
    
    // I'm not sure, if I need this
    public static String makeRoot(String fullURL, String redirect) {
        try {
            URL url = new URL(fullURL);
            String portString = (url.getPort() == -1) ? "" : ":" + url.getPort();
            String absRedirect = redirect.startsWith("/") ? redirect : "/" + redirect;
            return url.getProtocol() + "://" + url.getHost() + portString + absRedirect;
        } catch (MalformedURLException e) {
            return fullURL;
        }
    } 

}