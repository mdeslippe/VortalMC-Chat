package com.vortalmc.chat.utils.event.defined;

import com.vortalmc.chat.utils.event.Event;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * THe player first join event.
 * 
 * <p>
 * This event is called when a
 * {@link net.md_5.bungee.api.connection.ProxiedPlayer Player} joins the server
 * for the first time.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class PlayerFirstJoinEvent implements Event {

	/**
	 * The player that joined the server for the first time.
	 */
	private final ProxiedPlayer player;

	/**
	 * Create a new player first joined event.
	 * 
	 * @param player The player that joined the server for the first time.
	 */
	public PlayerFirstJoinEvent(ProxiedPlayer player) {
		this.player = player;
	}

	/**
	 * Get the player that joined the server for the first time.
	 * 
	 * @return The player that joined the server for the first time.
	 */
	public ProxiedPlayer getPlayer() {
		return this.player;
	}

}
