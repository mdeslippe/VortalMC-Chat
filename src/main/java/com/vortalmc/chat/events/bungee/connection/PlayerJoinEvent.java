package com.vortalmc.chat.events.bungee.connection;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all player join events.
 * 
 * @author Myles Deslippe
 */
public class PlayerJoinEvent implements Listener {

	/**
	 * This method will be called when a user connects to the server.
	 * 
	 * @param event The corresponding event variable.
	 */
	@EventHandler
	public void connected(ServerConnectedEvent event) {

		// Load the User's data into cache.
		User.fromProxiedPlayer(event.getPlayer()).getUserData();

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Display the player join message.
		for (String index : messages.getStringList("Events.Player-Join.Join-Message"))
			ProxyServer.getInstance().broadcast(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName())));
	}
}
