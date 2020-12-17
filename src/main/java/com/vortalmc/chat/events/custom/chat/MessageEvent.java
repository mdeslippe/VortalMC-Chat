package com.vortalmc.chat.events.custom.chat;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.event.EventHandler;
import com.vortalmc.chat.utils.event.Listener;
import com.vortalmc.chat.utils.event.defined.MessageSentEvent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * This class will handle the
 * {@link com.vortalmc.chat.utils.event.defined.MessageSentEvent
 * MessageSentEvent}.
 * 
 * @author Myles Deslippe
 */
public class MessageEvent implements Listener {

	@EventHandler
	public void onMessage(MessageSentEvent event) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

			if (event.getReceiver() == player || event.getSender() == player)
				continue;

			User user = User.fromProxiedPlayer(player);

			if (user.hasSocialSpyEnabled()) {
				for (String index : messages.getStringList("Commands.SocialSpy.Format")) {

					index = index.replace("${SENDER}", event.getSender().getName());
					index = index.replace("${RECEIVER}", event.getReceiver().getName());
					index = index.replace("${MESSAGE}", event.getMessage());

					player.sendMessage(new TextComponent(Utils.translateColor(index)));
				}
			}
		}
	}
}
