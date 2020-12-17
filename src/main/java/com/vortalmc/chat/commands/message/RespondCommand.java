package com.vortalmc.chat.commands.message;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.enums.ResponseMethod;

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
		User messager = User.fromProxiedPlayer(player);
		ResponseMethod method = ResponseMethod.valueOf(config.getString("Messages.Response-Method").toUpperCase());

		String validationBuffer = null;
		String type = "none";

		switch (method) {
		case LAST_MESSAGE_RECEIVER:
			validationBuffer = messager.getLastMessageReceiver();
			type = "Receiver";
			break;
		case LAST_MESSAGE_SENDER:
			validationBuffer = messager.getLastMessageSender();
			type = "Sender";
		}

		// Check if there is a player to respond to.
		if (validationBuffer.equalsIgnoreCase("none")) {
			for (String index : messages.getStringList("Commands.Respond.Has-No-Last-Message-" + type + "-Error"))
				player.sendMessage(new TextComponent(Utils.translateColor(index)));
			return;
		}

		target = ProxyServer.getInstance().getPlayer(UUID.fromString(validationBuffer));

		// If the player is offline
		if (target == null) {

			JsonObject data = new Gson().fromJson(Utils.getMojangPlayerData(UUID.fromString(validationBuffer)),JsonObject.class);

			for (String index : messages.getStringList("Commands.Message.Player-Not-Found"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", data.get("name").getAsString()))));
			return;
		}

		String[] buffer = new String[args.length + 1];

		buffer[0] = User.fromProxiedPlayer(target).getAsProxiedPlayer().getName();

		for (int i = 0; i <= args.length - 1; i++)
			buffer[i + 1] = args[i];

		// Send the response.
		new MessageCommand().onCommand(sender, buffer);
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
