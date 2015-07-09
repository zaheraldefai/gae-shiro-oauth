// Copyright (c) 2015 Zaher Aldefai. All Rights Reserved.
//
// File:        User.java 
// Author:      zaher

package de.zaheraldefai.samples.appengine_shiro_oauth.models.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.ByteSource;
import org.apache.shiro.util.SimpleByteSource;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


@Cache
@Entity
public class User implements Serializable {
	
	static final Logger LOG = Logger.getLogger(User.class.getName());
	
    public static final int HASH_ITERATIONS = 1;
    public static final String HASH_ALGORITHM = Sha256Hash.ALGORITHM_NAME;

    // TODO name ist kein richtig gute ID
	@Id
	private Long Id;
	
	// This can be also the Username
	@Index private String email;
	
	private String firstName;
	private String LastName;

	@JsonIgnore
	private String passwordHash;

	/**
	 * The salt, used to make sure that a dictionary attack is harder given a
	 * list of all the hashed passwords, as each salt will be different.
	 */
	private byte[] salt;

	private Set<String> roles;

	private Set<String> permissions;

	@Index
	private Date dateRegistered;

	private boolean isSuspended;

	/** For objectify to create instances on retrieval */
	private User() {
		this.roles = new HashSet<String>();
		this.permissions = new HashSet<String>();
	}

	public User(String email, String password, Set<String> roles, Set<String> permissions) {
		
		Preconditions.checkNotNull(email, "User email can't be null");
		Preconditions.checkNotNull(roles, "User roles can't be null");
		Preconditions.checkNotNull(permissions, "User permissions can't be null");
		
		this.email = email;
		this.salt = salt().getBytes();
		this.passwordHash = hash(password, salt);
		this.roles = Collections.unmodifiableSet(roles);
		this.permissions = Collections.unmodifiableSet(permissions);
		this.dateRegistered = new Date();
		this.isSuspended = false;
	}
	

	public Long getId() {
		return Id;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	public void setSuspended(boolean suspended) {
		isSuspended = suspended;
	}

	/**
	 * If you want to change the password! The Initial Password should be put through the Constructor.
	 */
	public void setPassword(String password) {
		Preconditions.checkNotNull(password);
		this.salt = salt().getBytes();
		this.passwordHash = hash(password, salt);
	}

	public Date getDateRegistered() {
		return dateRegistered;
	}

	@JsonIgnore
	public boolean isRegistered() {
		return getDateRegistered() != null;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return LastName;
	}

	public void setLastName(String lastName) {
		LastName = lastName;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	@JsonIgnore
	public byte[] getSalt() {
		return salt;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	private static ByteSource salt() {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		return rng.nextBytes();
	}

	private static String hash(String password, byte[] salt) {
		return (password == null) ? null : new Sha256Hash(password, new SimpleByteSource(salt), HASH_ITERATIONS).toHex();
	}

    // TODO name wird verglichen, sollte andere ID
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) {
			User u = (User) o;
			return Objects.equal(getEmail(), u.getEmail())
					&& Objects.equal(getPasswordHash(), u.getPasswordHash());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(email, passwordHash);
	}

	public String toJSONString() {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String out = mapper.writeValueAsString(this);
			return out;
		} catch (JsonProcessingException e) {
			LOG.severe("Can't convert GaeUser " + this + " to JSON string");
		}
		return null;
	}
}
