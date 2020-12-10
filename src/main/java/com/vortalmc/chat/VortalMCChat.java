package com.vortalmc.chat;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

/**
 * The VortalMC-Chat plugin's main class.
 * 
 * @author Myles Deslippe
 */
public class VortalMCChat extends Plugin {

	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {

	}

	/**
	 * Called when the plugin is disabled.
	 */
	public void onDisable() {

	}

	/**
	 * Get an instance of the VortalMC-Chat plugin.
	 * 
	 * <p>
	 * Note: This will return null if the plugin is not loaded.
	 * </p>
	 * 
	 * @return The VortalMC-Chat plugin instance
	 */
	public static Plugin getInstance() {
		return ProxyServer.getInstance().getPluginManager().getPlugin("VortalMC-Chat");
	}
}
