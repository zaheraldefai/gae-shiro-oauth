// Copyright (c) 2015 Zaher Aldefai. All Rights Reserved.
//
// File:        AuthAPI.java 
// Author:      zaher

package de.zaheraldefai.samples.appengine_shiro_oauth;


import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.mgt.SecurityManager;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Nullable;


/**
  * Add your first API methods in this class, or you may create another class. In that case, please
  * update your web.xml accordingly.
 **/
@Api(name = "authApi",
version = "v1")
public class AuthAPI {
	
	private static final Logger LOG = Logger.getLogger(AuthAPI.class.getName());
	
	
	/**
	 * This method is just an dummy method to get an generated client method for the login.
	 * The real login process is processed in a Filter class
	 * (See {@link com.tutorials.gaeshiro.security.filter.OAuthFilter}).
	 * If this method is called, then it means, that the login is successful.
	 * 
	 * @param provider (facebook is surpported)
     * @param accessToken (optional)
	 */
    @ApiMethod(name = "login_oauth", httpMethod = "get", path="login/oauth")
    public void login_oauth(@Named("provider") String provider, @Named("access_token") @Nullable String accessToken, @Named("redirectURL") @Nullable String redirectURL) {
    	
    	LOG.info("Success login. provider: " + provider);
    }
	
    @ApiMethod(name = "logout", httpMethod = "get")
    public void logout() {
    	
    	Factory<SecurityManager> factory = new IniSecurityManagerFactory();
        SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    	
    	Subject subject = SecurityUtils.getSubject();
    	
		subject.logout();
    }
}
