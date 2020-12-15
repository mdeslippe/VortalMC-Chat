package com.vortalmc.chat.commands.nickname.subcommands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat nickname command.
 * 
 * @author Myles Deslippe
 */
public class NicknameOtherCommand extends CommandListener {

	public NicknameOtherCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Subcommands.Other.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Subcommands.Other.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Nickname.Subcommands.Other.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Query mojang for playdata.
		String response = Utils.getMojangPlayerData(args[0]);

		// If there was no response.
		if (response == null) {
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
			
			return;
		}

		UUID uuid = UUID.fromString(Utils.formatUUID(new Gson().fromJson(response, JsonObject.class).get("id").getAsString()));
		User user = User.fromUUID(uuid);
			
		// Check if the player has joined the server before.
		if (!user.isInDatabase()) {
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
				
		// Check if the sender is trying to remove the player's nickname
		} else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("none")) {

			user.removeNickname();

			for (String index : messages.getStringList("Commands.Nickname.Other.Removed"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));

		// Change the player's nickname.
		} else {

			user.setNickname(args[1]);

			for (String index : messages.getStringList("Commands.Nickname.Other.Updated"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${NICKNAME}", args[1]).replace("${PLAYER}", args[0]))));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
