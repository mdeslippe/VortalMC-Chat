package com.vortalmc.chat.commands.message;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.enums.ResponseMethod;
import com.vortalmc.chat.utils.event.defined.MessageSentEvent;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat respond command.
 * 
 * @author Myles Deslippe
 */
public class RespondCommand extends CommandListener {

	public RespondCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Respond.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Respond.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Respond.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("Error: You muse be a player to use this command!"));
			return;
		}
		
		ProxiedPlayer player = (ProxiedPlayer) sender, target = null;
		User user = User.fromProxiedPlayer(player);
		ResponseMethod method = ResponseMethod.valueOf(config.getString("Messages.Response-Method").toUpperCase());
		String uuid = null;
		
		switch (method) {
		case LAST_MESSAGE_RECEIVER:
			uuid = user.getLastMessageReceiver();
			
			if(uuid.equalsIgnoreCase("none")) {
				for(String index : messages.getStringList("Commands.Respond.Has-No-Last-Message-Receiver-Error"))
					player.sendMessage(new TextComponent(Utils.translateColor(index)));
				return;
			}
			
			target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
			break;
		case LAST_MESSAGE_SENDER:
			uuid = user.getLastMessageSender();
			
			if(uuid.equalsIgnoreCase("none")) {
				for(String index : messages.getStringList("Commands.Respond.Has-No-Last-Message-Sender-Error"))
					player.sendMessage(new TextComponent(Utils.translateColor(index)));
				return;
			}
			
			target = ProxyServer.getInstance().getPlayer(UUID.fromString(uuid));
			break;
		}
		
		// If the player is offline
		if(target == null) {

			JsonObject data = new Gson().fromJson(Utils.getMojangPlayerData(UUID.fromString(uuid)), JsonObject.class);
			
			for(String index : messages.getStringList("Commands.Message.Player-Not-Found"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", data.get("name").getAsString()))));
				
			return;
		}
			
		String message = String.join(" ", args);
		User targetUser = User.fromProxiedPlayer(target);

		// Send the sender the message
		for (String index : messages.getStringList("Commands.Message.Send-Format"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index
					.replace("${SENDER}", sender.getName())
					.replace("${RECEIVER}", target.getName())
					.replace("${MESSAGE}", message))
					));
		
		// Send the target the message.
		for (String index : messages.getStringList("Commands.Message.Receive-Format"))
			target.sendMessage(new TextComponent(Utils.translateColor(index
					.replace("${SENDER}", sender.getName())
					.replace("${RECEIVER}", target.getName())
					.replace("${MESSAGE}", message))
					));
		
		user.setLastMessageReceiver(target.getUniqueId());
		targetUser.setLastMessageSender(player.getUniqueId());
		
		VortalMCChat.getInstance().getInternalEventManager().dispatchEvent(new MessageSentEvent(player, target, message));
		
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
