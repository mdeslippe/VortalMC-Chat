package com.vortalmc.chat.utils.channel;

/**
 * The Channel class.
 * 
 * <p>
 * Extend this class to implement channels.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class Channel {

	/**
	 * The channel's name.
	 */
	private String name;

	/**
	 * The permission required to talk in the channel.
	 */
	private String permission;

	/**
	 * The message format that will be used when a message is sent to the channel.
	 */
	private String format;

	/**
	 * The channel's aliases.
	 */
	private String[] aliases;

	/**
	 * The channel's scope.
	 */
	private ChannelScope scope;

	/**
	 * Create a new abstract channel.
	 * 
	 * @param name       The name of the channel.
	 * @param permission The permission required to talk in the channel.
	 * @param format     The format that will be used when a message is sent to the
	 *                   channel.
	 */
	public Channel(String name, String permission, String format, String[] aliases, ChannelScope scope) {
		this.name = name;
		this.permission = permission;
		this.format = format;
		this.aliases = aliases;
		this.scope = scope;
	}

	/**
	 * Get the channel's name.
	 * 
	 * @return The name of the channel.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Set the channel's name.
	 * 
	 * @param name The channel's new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the permission required to speak in the channel.
	 * 
	 * @return The permission required to speak in the channel.
	 */
	public String getPermission() {
		return this.permission;
	}

	/**
	 * Set the permission required to speak in the channel.
	 * 
	 * @param permission THe new permission required to speak in the channel.
	 */
	public void setPermission(String permission) {
		this.permission = permission;
	}

	/**
	 * Get the channel's message format.
	 * 
	 * @return The channel's message format.
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * Set the channel's message format.
	 * 
	 * @param format The channel's new message format.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * Get the channel's aliases.
	 * 
	 * @return The channel's aliases.
	 */
	public String[] getAliases() {
		return this.aliases;
	}

	/**
	 * Set the channel's aliases.
	 * 
	 * @param aliases The channel's new aliases.
	 */
	public void setAliases(String[] aliases) {
		this.aliases = aliases;
	}

	/**
	 * Get the channell's scope.
	 * 
	 * @return The channel's scope.
	 */
	public ChannelScope getChannelScope() {
		return this.scope;
	}

	/**
	 * Set the channel's scope.
	 * 
	 * @param scope The channel's new scope.
	 */
	public void setChannelScope(ChannelScope scope) {
		this.scope = scope;
	}

	/**
	 * Check if the channel has an alias.
	 * 
	 * @param alias The alias to check for.
	 * 
	 * @return The truth value associated with the existance of the alias.
	 */
	public boolean containsAlias(String alias) {

		for (String index : this.aliases)
			if (index == alias)
				return true;

		return false;
	}

}
