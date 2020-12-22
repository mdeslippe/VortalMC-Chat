package com.vortalmc.chat.commands.socialspy.subcommands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat social spy command.
 * 
 * <p>
 * This class is specifically dedicated to toggling socialspy for other users.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class SocialSpyOtherCommand extends CommandListener {

	public SocialSpyOtherCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("SocialSpy.Subcommands.Other.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("SocialSpy.Subcommands.Other.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("SocialSpy.Subcommands.Other.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Attempt to get the target's player data from mojang.
		String mojangPlayerData = Utils.getMojangPlayerData(args[0]);

		// If the response is null, that implies the player does not exist, or the
		// authentication servers are offline. In either case the target's social spy
		// will not be updated.
		if (mojangPlayerData == null) {

			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));

			return;
		}

		// Load the player data.
		UUID uuid = UUID.fromString(Utils.formatUUID(new Gson().fromJson(mojangPlayerData, JsonObject.class).get("id").getAsString()));
		User target = User.fromUUID(uuid);

		// Check if the player is in the database.
		if (!target.isInDatabase()) {
			
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));

			return;
		}
		
		// Toggle the target's social spy.
		if (target.hasSocialSpyEnabled()) {

			target.disableSocialSpy();

			for (String index : messages.getStringList("Commands.SocialSpy.Other.Disabled"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));

		} else {

			target.enableSocialSpy();

			for (String index : messages.getStringList("Commands.SocialSpy.Other.Enabled"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}

}
