// Copyright (c) 2015 Zaher Aldefai. All Rights Reserved.
//
// File:        SomeAPI.java 
// Author:      zaher

package de.zaheraldefai.samples.appengine_shiro_oauth;


import java.util.logging.Logger;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;

import de.zaheraldefai.samples.appengine_shiro_oauth.models.others.TestModelBean;


/**
  * Add your first API methods in this class, or you may create another class. In that case, please
  * update your web.xml accordingly.
 **/
@Api(name = "someApi",
version = "v1")
public class SomeAPI {
	
	private static final Logger LOG = Logger.getLogger(SomeAPI.class.getName());
	
    
    /**
     * Just for Testing the Clients
     */
    @ApiMethod(name = "getApi", path="getApi", httpMethod = "get")
    public void getApi(HttpServletRequest req) {
    	String headerMessage =  req.getHeader("Authorization");
    	
    	if(headerMessage == null){
    		LOG.info("success: getApi");
    	}
    	LOG.info("success: getApi. Header(Authorization): " + req.getHeader("Authorization"));
    }
    
    @ApiMethod(name = "getApiWithParameter", path="getApiWithParameter", httpMethod = "get")
    public void getApiWithParameter(@Named("someData") String someData) {
    	
    	LOG.info("success: getApiWithParameter = " + someData);
    }
    
    @ApiMethod(name = "getApiWithResult", path="getApiWithResult", httpMethod = "get")
    public TestModelBean getApiWithResult() {
    	
    	return new TestModelBean("someData");
    }
    
    

    
    
}
