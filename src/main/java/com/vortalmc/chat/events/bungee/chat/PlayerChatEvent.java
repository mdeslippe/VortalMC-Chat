package com.vortalmc.chat.events.bungee.chat;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.utils.event.defined.CommandExecutedEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all player chat events.
 * 
 * @author Myles Deslippe
 */
public class PlayerChatEvent implements Listener {

	/**
	 * This method will handle the chat event.
	 * 
	 * @param event The corresponding event variable.
	 */
	@EventHandler
	public void onChatEvent(ChatEvent event) {

		// If the event was a command.
		if (event.getMessage().startsWith("/") || event.isCancelled()) {
			VortalMCChat.getInstance().getInternalEventManager().dispatchEvent(new CommandExecutedEvent(event.getSender(), event.getMessage()));
		} else {
			ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			VortalMCChat.getInstance().dispatchMessage(player, event.getMessage());
			event.setCancelled(true);
		}
	}
}
