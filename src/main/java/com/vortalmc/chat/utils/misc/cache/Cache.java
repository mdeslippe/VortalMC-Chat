package com.vortalmc.chat.utils.misc.cache;

/**
 * A cache interface.
 * 
 * <p>
 * Implement this in class you wish to make cache.
 * </p>
 * 
 * @author Myles Deslippe
 */
public interface Cache {

	/**
	 * This method will be called when they cached needs to be pushed to the
	 * database.
	 * 
	 * @throws Exception Any exception that occurs when the cache is being pushed.
	 */
	public void push() throws Exception;

}
