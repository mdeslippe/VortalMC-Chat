package com.vortalmc.chat.events.bungee.connection;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all server switch events.
 * 
 * @author Myles Deslippe
 */
public class PlayerServerSwitchEvent implements Listener {

	/**
	 * This method will be called when a user switches servers.
	 * 
	 * @param event The corresponding event variable.
	 */
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent event) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check to see if the player is joining the network or switching servers.
		// If the getFrom() method is null, that implies the player is joining the
		// network as opposed to switching server, therefore we do not want to handle
		// the server switch event.
		if (event.getFrom() != null) {

			for (String index : messages.getStringList("Events.Server-Switched.Switch-Message")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${PLAYER}", event.getPlayer(), false);
				msg.replace("${SERVER}", event.getPlayer().getServer().getInfo().getName(), false);

				ProxyServer.getInstance().broadcast(msg.build());
			}
		}
	}

}
