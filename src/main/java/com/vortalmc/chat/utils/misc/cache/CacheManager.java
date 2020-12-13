package com.vortalmc.chat.utils.misc.cache;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A cache management utility.
 * 
 * <p>
 * Use this cache manager to manage
 * {@link com.vortalmc.chat.utils.misc.cache.Cache Cache}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class CacheManager {

	/**
	 * An ArrayList storing the cache.
	 */
	private final HashMap<Object, Cache> cache;

	/**
	 * Create a CacheManager.
	 */
	public CacheManager() {
		cache = new HashMap<Object, Cache>();
	}

	/**
	 * Get cache that is currently being stored.
	 * 
	 * @param key The key that is associated with the cache.
	 * 
	 * @return The cache.
	 */
	public Cache getCache(Object key) {
		return this.cache.get(key);
	}

	/**
	 * Add cache.
	 * 
	 * @param cache The cache to add.
	 */
	public void addCache(Object key, Cache cache) {
		this.cache.put(key, cache);
	}

	/**
	 * Remove cache.
	 * 
	 * @param cache The cache to remove.
	 */
	public void removeCache(Cache cache) {
		if (this.containsCache(cache))
			this.cache.remove(cache);
	}

	/**
	 * Check if cache exists.
	 * 
	 * @param key The key associated with the cache to check for.
	 * 
	 * @return The truth value associated with the key existing.
	 */
	public boolean containsCache(Object key) {
		return this.cache.containsKey(key);
	}

	/**
	 * Check if cache exists.
	 * 
	 * @param cache The cache to check for.
	 * 
	 * @return The truth value associated with the cache existing.
	 */
	public boolean containsCache(Cache cache) {
		return this.cache.containsValue(cache);
	}

	/**
	 * Get all of the cache.
	 * 
	 * @return An ArrayList containing all of the cache.
	 */
	public HashMap<Object, Cache> getCache() {
		return this.cache;
	}

	/**
	 * Clear all of the cache.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Push all cached.
	 * 
	 * @throws Exception If any exception occurs when attempting to push the cache.
	 */
	public void pushAllCache() throws Exception {

		Iterator<Entry<Object, Cache>> iterator = cache.entrySet().iterator();

		while (iterator.hasNext())
			iterator.next().getValue().push();
	}
}
