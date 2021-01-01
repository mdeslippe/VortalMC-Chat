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

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		User user = User.fromProxiedPlayer(event.getPlayer());

		// Check if the player has played the server before.
		if (user.isInDatabase()) {

			// Load the User's data into cache.
			user.getUserData();

			for (String index : messages.getStringList("Events.Player-Join.Join-Message"))
				ProxyServer.getInstance().broadcast(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName())));

		} else {

			// Create and then load the User's data into cache.
			user.getUserData();

			for (String index : messages.getStringList("Events.Player-Join.First-Join-Message"))
				ProxyServer.getInstance().broadcast(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName())));
		}
	}
}
