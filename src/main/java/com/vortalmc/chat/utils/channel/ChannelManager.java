package com.vortalmc.chat.utils.channel;

import java.util.HashMap;

/**
 * A {@link com.vortalmc.chat.utils.channel.AbstractChannel Channel} management
 * utility.
 * 
 * @author Myles Deslippe
 */
public class ChannelManager {

	/**
	 * The regsitered channels.
	 */
	private HashMap<String, AbstractChannel> channels;

	/**
	 * Create a new channel manager.
	 */
	public ChannelManager() {
		this.channels = new HashMap<String, AbstractChannel>();
	}

	/**
	 * Get a channel.
	 * 
	 * @param name The name of the channel.
	 * 
	 * @return The channel.
	 */
	public AbstractChannel getChannel(String name) {
		return this.channels.get(name.toLowerCase());
	}

	/**
	 * Register a channel with the channel manager.
	 * 
	 * <p>
	 * By default, the key to index the channel will be the channel
	 * {@link com.vortalmc.chat.utils.channel.AbstractChannel#getName() name}. If
	 * you wish to define a custom key, use
	 * {@link #registerChannel(String, AbstractChannel)}.
	 * </p>
	 * 
	 * @param channel The channel to register.
	 */
	public void registerChannel(AbstractChannel channel) {
		this.channels.put(channel.getName().toLowerCase(), channel);
	}

	/**
	 * Register a channel with the channel manager.
	 * 
	 * @param key     The key to index the channel.
	 * @param channel The channel to register.
	 */
	public void registerChannel(String key, AbstractChannel channel) {
		this.channels.put(key.toLowerCase(), channel);
	}

	/**
	 * Unregister a channel from the channel manager.
	 * 
	 * @param channel The channel to unregister.
	 */
	public void unregisterChannel(String channel) {
		this.channels.remove(channel.toLowerCase());
	}

	/**
	 * Check if a channel is registered with the channel manager.
	 * 
	 * @param channel The channel to check for.
	 * 
	 * @return The truth value assocated with the registration of the channel.
	 */
	public boolean containsChannel(String channel) {
		return this.channels.containsKey(channel.toLowerCase());
	}

	/**
	 * Check if a channel is registered with the channel manager.
	 * 
	 * @param channel The channel to check for.
	 * 
	 * @return The truth value assocated with the registration of the channel.
	 */
	public boolean containsChannel(AbstractChannel channel) {
		return this.channels.containsValue(channel);
	}

	/**
	 * Unregister all channels from the channel manager.
	 */
	public void unregisterAllChannels() {
		this.channels.clear();
	}

	/**
	 * Get a map of all the registered channels.
	 * 
	 * <p>
	 * Note: This is a clone of the original map, modifications made to the elements
	 * of the map will affect the origional elements.
	 * </p>
	 * 
	 * @return The map of registered channels.
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, AbstractChannel> getChannels() {
		return (HashMap<String, AbstractChannel>) this.channels.clone();
	}

}
