package com.vortalmc.chat.events.bungee.connection;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.mysql.CachedRow;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all player leave events.
 * 
 * @author Myles Deslippe
 */
public class PlayerLeaveEvent implements Listener {

	/**
	 * This method will be called when a user disconnection from the server.
	 * 
	 * @param event The corresponding event variable.
	 */
	@EventHandler
	public void onDisconnect(PlayerDisconnectEvent event) {
		
		User user = User.fromProxiedPlayer(event.getPlayer());

		// Push the user's cache to the database, and display the leave message. If the
		// user's data is not cached it means they did not fully connect to the server, so
		// we do not want to display the player leave message.
		try {
			if (user.dataIsCached()) {
				CachedRow row = user.getUserData();
				row.push();
				VortalMCChat.getInstance().getCacheManager().removeCache(row);
				
				Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

				// Display the player leave message.
				for (String index : messages.getStringList("Events.Player-Leave.Leave-Message"))
					ProxyServer.getInstance().broadcast(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName())));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}