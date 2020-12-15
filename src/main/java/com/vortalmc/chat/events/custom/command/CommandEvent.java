package com.vortalmc.chat.events.custom.command;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.event.EventHandler;
import com.vortalmc.chat.utils.event.Listener;
import com.vortalmc.chat.utils.event.defined.CommandExecutedEvent;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * This class will handle the
 * {@link com.vortalmc.chat.utils.event.defined.CommandExecutedEvent
 * CommandExecutedEvent}.
 * 
 * @author Myles Deslippe
 */
public class CommandEvent implements Listener {

	@EventHandler
	public void onMessage(CommandExecutedEvent event) {
		
		if(!(event.getExecutor() instanceof ProxiedPlayer))
				return;
		
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		ProxiedPlayer executor = (ProxiedPlayer) event.getExecutor();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {

			if (event.getExecutor() == player)
				continue;

			User user = User.fromProxiedPlayer(player);

			if (user.hasCommandSpyEnabled()) {
				for (String index : messages.getStringList("Commands.CommandSpy.Format")) {

					index = index.replace("${SENDER}", executor.getName());
					index = index.replace("${COMMAND}", event.getCommand());

					player.sendMessage(new TextComponent(Utils.translateColor(index)));
				}
			}
		}
	}
}
