package com.vortalmc.chat.events.custom.connection;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.event.EventHandler;
import com.vortalmc.chat.utils.event.Listener;
import com.vortalmc.chat.utils.event.defined.PlayerFirstJoinEvent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

/**
 * This class will handle the
 * {@link com.vortalmc.chat.utils.event.defined.PlayerFirstJoinEvent
 * PlayerFirstJoinEvent}.
 * 
 * @author Myles Deslippe
 */
public class FirstJoinEvent implements Listener {

	/**
	 * This method will be called when a player joins the server for the first time.
	 * 
	 * @param event The corresponding event variable.
	 */
	@EventHandler
	public void onFirstJoin(PlayerFirstJoinEvent event) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Events.Player-Join.First-Join-Message"))
			ProxyServer.getInstance().broadcast(Utils.translateColor(index.replace("${PLAYER}", event.getPlayer().getName())));
	}
}
