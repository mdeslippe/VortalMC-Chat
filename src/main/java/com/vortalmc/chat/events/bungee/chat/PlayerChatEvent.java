package com.vortalmc.chat.events.bungee.chat;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.event.defined.CommandExecutedEvent;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all player chat events.
 * 
 * <p>
 * <strong>Note:</strong> Command executions are considered chat events, hence
 * they will be handeled here.
 * </p>
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

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// If the command sender was not a player.
		if (!(event.getSender() instanceof ProxiedPlayer))
			return;

		// Check if the event was a command, and handle accordingly.
		if (event.getMessage().startsWith("/")) {

			VortalMCChat.getInstance().getInternalEventManager().dispatchEvent(new CommandExecutedEvent(event.getSender(), event.getMessage()));

			return;
		}

		// Check if the sender has chat enabled.
		if (!User.fromProxiedPlayer((ProxiedPlayer) event.getSender()).hasChatEnabled()) {

			for (String index : messages.getStringList("Error.Chat-Not-Enabled"))
				((ProxiedPlayer) event.getSender()).sendMessage(Utils.translateColor(index));

			event.setCancelled(true);
			
			return;
		}

		// Since all checks have passed, dispatch the message.
		VortalMCChat.getInstance().dispatchMessage((ProxiedPlayer) event.getSender(), event.getMessage());
		event.setCancelled(true);
	}

}
