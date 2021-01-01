package com.vortalmc.chat.commands.prefix.subcommands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
import com.vortalmc.chat.users.meta.exceptions.LengthException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat prefix command.
 * 
 * <p>
 * This class is specifically dedicated to changing the prefix of other players.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class PrefixOtherCommand extends CommandListener {

	public PrefixOtherCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Prefix.Subcommands.Other.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Prefix.Subcommands.Other.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Prefix.Subcommands.Other.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		Configuration permissions = VortalMCChat.getInstance().getFileManager().getFile("permissions").getConfiguration();
		
		// Attempt to get the target's player data from mojang.
		String mojangPlayerData = Utils.getMojangPlayerData(args[0]);

		// If the response is null, that implies the player does not exist, or the
		// authentication servers are offline. In either case the prefix will not be
		// updated.
		if (mojangPlayerData == null) {
			
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));
			
			return;
		}

		// Load player from the mojang player data.
		UUID uuid = UUID.fromString(Utils.formatUUID(new Gson().fromJson(mojangPlayerData, JsonObject.class).get("id").getAsString()));
		User target = User.fromUUID(uuid);

		// Check if the player has joined the server before.
		if (!target.isInDatabase()) {
			
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));
			
			return;
		}

		// Check if the sender is trying to remove the target's prefix.
		if (args[1].equalsIgnoreCase("none") || args[1].equalsIgnoreCase("off")) {

			target.getMeta().setPrefix("none");

			for (String index : messages.getStringList("Commands.Prefix.Other.Removed"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));
			
			return;
		}

		// Validate the prefix.
		try {
			
			VortalMCChat.getInstance().getMetaValidator().validatePrefix(args[1]);
			
		} catch (ForbiddenTextException e) {

			for (String index : messages.getStringList("Commands.Prefix.Forbidden-Character")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${CHARACTER}", e.getForbiddenText(), false);

				sender.sendMessage(msg.build());
			}

			return;
			
		} catch (LengthException e) {

			String path = "Commands.Prefix.";

			switch (e.getLengthExceptionType()) {
			case TOO_BIG:
				path += "Prefix-Too-Big";
				break;
			case TOO_SMALL:
				path += "Prefix-Too-Small";
				break;
			}

			for (String index : messages.getStringList(path)) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${PREFIX}", args[1], false);
				msg.replace("${MINSIZE}", String.valueOf(config.getInt("Prefix.Minimum-Length")), false);
				msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Prefix.Maximum-Length")), false);

				sender.sendMessage(msg.build());
			}

			return;
		}

		// Update the validated prefix.
		target.getMeta().setPrefix(args[1]);

		for (String index : messages.getStringList("Commands.Prefix.Other.Updated")) {

			MessageBuilder msg = new MessageBuilder(index);
			msg.replace("${PLAYER}", args[0], false);
			msg.replace("${PREFIX}", args[1], sender.hasPermission(permissions.getString("Color.Prefix")));

			sender.sendMessage(msg.build());
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
