package com.vortalmc.chat.utils.misc.cache;

import java.util.ArrayList;
import java.util.Iterator;

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
	private final ArrayList<Cache> cache;

	/**
	 * Create a CacheManager.
	 */
	public CacheManager() {
		cache = new ArrayList<Cache>();
	}

	/**
	 * Add cache.
	 * 
	 * @param cache The cache to add.
	 */
	public void addCache(Cache cache) {
		this.cache.add(cache);
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
	 * @param cache The cache to check for.
	 * 
	 * @return The truth value associated with the cache existing.
	 */
	public boolean containsCache(Cache cache) {
		return this.cache.contains(cache);
	}

	/**
	 * Get all of the cache.
	 * 
	 * @return An ArrayList containing all of the cache.
	 */
	public ArrayList<Cache> getCache() {
		return this.cache;
	}

	/**
	 * Clear all of the cache.
	 */
	public void clear() {
		cache.clear();
	}

	/**
	 * Get an iterator containing the cache.
	 * 
	 * @return An iterator containing the cache.
	 */
	public Iterator<Cache> getCacheIterator() {
		return cache.iterator();
	}

}
