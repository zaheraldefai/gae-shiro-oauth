// Copyright (c) 2015 Zaher Aldefai. All Rights Reserved.
//
// File:        UserDAO.java 
// Author:      zaher

package de.zaheraldefai.samples.appengine_shiro_oauth.models.user;

import com.googlecode.objectify.ObjectifyService;

import de.zaheraldefai.samples.appengine_shiro_oauth.models.BaseDAO;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class UserDAO extends BaseDAO<User> {
	
    static final Logger LOG = Logger.getLogger(UserDAO.class.getName());
    
    private static final long REGISTRATION_VALID_DAYS = 1;

    static {
        ObjectifyService.register(User.class);
    }

    public UserDAO() {
        super(User.class);
    }

    /**
     * Save user with authorization information
     * @param user  User
     * @param changeCount should the user count be incremented
     */
    public void saveUser(User user) {
    	
    	beginOfy();
    	put(user);
    	endOfy();
    	
        
        // TODO Send an Email to give the User to validate himself
    }

    public void deleteUser(User user) {
    	
    	beginOfy();
    	delete(user.getId());
    	endOfy();
    }
    
    public void deleteUser(String email) {
    	
    	beginOfy();
    	User user = getByAttribute("email", email);
    	if(user!=null){
    		delete(user.getId());
    	}else{
    		LOG.info("There is no User to delete with this email/username: " + email);
    	}
    	
    	endOfy();
    }
    
    public User findUser(Long id) {

    	beginOfy();
    	User u = get(id);
    	endOfy();
    	
    	return u;
    }
    
    public User findUser(String email) {
    	
    	beginOfy();
    	User u = getByAttribute("email", email);
    	endOfy();
    	
    	return u;
    }
    


}