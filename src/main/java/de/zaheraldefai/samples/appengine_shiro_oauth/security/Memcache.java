// Copyright (c) 2012 Tim Niblett. All Rights Reserved.
//
// File:        Memcache.java  (07-Oct-2012)
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

package de.zaheraldefai.samples.appengine_shiro_oauth.security;

import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Shiro cache for App Engine memcached.  Some simplifications are made as Memcached
 * is global and these caches are meant to be local, with a name and all.  I've sorted
 * this by wrapping keys in a class to avoid string clashes.  We lose efficiency of course
 * as it will get serialized.
 * <p> I'm using the asynchronous service as it will be a little faster on the puts.
 * <p> Memcache on GAE is pretty slow, so I don't think this will speed stuff up, but it
 * will save on Datastore accesses, which are expensive.
 * @param <K>  The raw key class
 * @param <V>  The value class
 */
public class Memcache<K, V> implements Cache<K, V> {

    private static final int EXPIRES = 300; // default to 5 minutes

    private final String name;
    private final AsyncMemcacheService memcacheService;

    Memcache(String name) {
        this.name = name;
        this.memcacheService = MemcacheServiceFactory.getAsyncMemcacheService();
    }

    @SuppressWarnings("unchecked")
    public V get(K k) throws CacheException {
        try {
            return (V) memcacheService.get(wrap(k)).get();
        } catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    public V put(K k, V v) throws CacheException {
        memcacheService.put(wrap(k), v, Expiration.byDeltaSeconds(EXPIRES));
        return null;
    }
    
    public V putSync(K k, V v) throws CacheException {
        try {
            memcacheService.put(wrap(k), v, Expiration.byDeltaSeconds(EXPIRES)).get();
            return null;
        }  catch (InterruptedException e) {
            return null;
        } catch (ExecutionException e) {
            return null;
        }
    }

    public V remove(K k) throws CacheException {
        memcacheService.delete(wrap(k));
        return null;
    }

    public void clear() throws CacheException {
        // can't do this as it would clear the whole cache for this app, which could be very expensive
    }

    public int size() {
        return 0; // we can't get a count of just our keys
    }

    public Set<K> keys() {
        return new HashSet<K>(); // you just can't list a distributed cache
    }

    public Collection<V> values() {
        return new HashSet<V>(); // you can't list a distributed cache
    }

    private Wrap wrap(K k) {
        return new Wrap<K>(name, k);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static class Wrap<K> implements Serializable {
        // note that keys will be lost if we change anything as we haven't set serialVersionUID (not necessary)
        private String name;
        private K wrapped;

        Wrap(String name, K wrapped) {
            this.name = name;
            this.wrapped = wrapped;
        }
    }
}