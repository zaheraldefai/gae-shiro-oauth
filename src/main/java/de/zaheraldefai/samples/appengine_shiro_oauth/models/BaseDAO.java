// Copyright (c) 2015 Zaher Aldefai. All Rights Reserved.
//
// File:        UserDAO.java 
// Author:      zaher

package de.zaheraldefai.samples.appengine_shiro_oauth.models;


import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.Closeable;

import de.zaheraldefai.samples.appengine_shiro_oauth.models.user.User;

import java.util.logging.Logger;

import static com.googlecode.objectify.ObjectifyService.ofy;

public class BaseDAO<T> {
    static final Logger LOG = Logger.getLogger(BaseDAO.class.getName());

    private final Class clazz;
    
    protected Closeable session;

    public BaseDAO(Class clazz) {
        this.clazz = clazz;
    }
    
    /**
     * Beginn an Session for a Ofy Datastore Connection.
     * Needed since Objectivity 5.1.x
     */
    public void beginOfy(){
        	session = ObjectifyService.begin();
    }
    public void endOfy(){
    	if(session != null){
        	session.close();
        	session = null;
    	}
    }

    @SuppressWarnings({"unchecked"})
    public T get(Long id) {
        
        T db = (T) ofy().load().type(clazz).id(id).now();
                
        return db;
    }
    
    /**
     * Filter an Entity Object which have an Attribute (AttributeName) with a special Value.
     * In this Method, the Attribute Type just can be an String!
     * 
     * @param AttributeName
     * @param Value
     * @return The Entity
     */
    @SuppressWarnings({"unchecked"})
    public T getByAttribute(String attributeName, String value) {
    	
        T dp = (T) ofy().load().type(clazz).filter(attributeName, value).first().now();

        return dp;
    }
    
    @SuppressWarnings({"unchecked"})
    public T getByAttribute(String AttributeName, Long value) {
    	
        T dp = (T) ofy().load().type(clazz).filter(AttributeName, value).first().now();

        return dp;
    }
    
    

    public void put(T object) {
        ofy().save().entity(object).now();
    }

    @SuppressWarnings({"unchecked"})
    public void delete(Long id) {
        ofy().delete().key(Key.create(clazz, id));
    }

}