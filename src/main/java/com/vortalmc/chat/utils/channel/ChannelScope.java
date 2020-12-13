package com.vortalmc.chat.utils.channel;

/**
 * The channel scope enum.
 * 
 * <p>
 * These scops are used to define
 * {@link com.vortalmc.chat.utils.channel.AbstractChannel Channel} scopes.
 * <p>
 * 
 * @author Myles Deslippe
 */
public enum ChannelScope {

	/**
	 * This scope dispatches the message across the entire proxy server.
	 */
	GLOBAL,

	/**
	 * This scope dispatches the message on the local spigot server the sender is
	 * on.
	 */
	LOCAL;

}
