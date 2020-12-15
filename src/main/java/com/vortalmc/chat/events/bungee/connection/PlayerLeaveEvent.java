package com.vortalmc.chat.events.bungee.connection;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.mysql.CachedRow;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
		
		try {
			if (user.dataIsCached()) {
				CachedRow row = user.getUserData();
				row.push();
				VortalMCChat.getInstance().getCacheManager().removeCache(row);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Events.Player-Leave.Leave-Message"))
			ProxyServer.getInstance().broadcast(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName()))));
	}
}