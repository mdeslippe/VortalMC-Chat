package com.vortalmc.chat.commands.nickname.subcommands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
import com.vortalmc.chat.users.meta.exceptions.LengthException;
import com.vortalmc.chat.users.meta.exceptions.NicknameCannotBePlayerNameException;
import com.vortalmc.chat.users.meta.exceptions.NicknameInUseException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat nickname command.
 * 
 * <p>
 * This class is specifically dedicated for changing the nickname of other
 * players.
 * </p>
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
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		String mojangPlayerData = Utils.getMojangPlayerData(args[0]);

		// If there was no response.
		if (mojangPlayerData == null) {
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
			return;
		}

		// Load player from the mojang player data.
		UUID uuid = UUID.fromString(Utils.formatUUID(new Gson().fromJson(mojangPlayerData, JsonObject.class).get("id").getAsString()));
		User target = User.fromUUID(uuid);

		// Check if the player has joined the server before.
		if (!target.isInDatabase()) {
			for (String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));

			// Check if the sender is trying to remove the player's nickname
		} else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("none")) {

			target.getMeta().removeNickname();

			for (String index : messages.getStringList("Commands.Nickname.Other.Removed"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));

			// Change the player's nickname.
		} else {

			try {
				VortalMCChat.getInstance().getMetaValidator().validateNickname(args[1]);
			} catch (ForbiddenTextException e) {
				for (String msg : messages.getStringList("Commands.Nickname.Forbidden-Character")) {

					MessageBuilder buffer = new MessageBuilder(msg);
					buffer.replace("${CHARACTER}", e.getText(), false);

					sender.sendMessage(buffer.build());
				}

				return;
			} catch (NicknameInUseException e) {
				for (String index : messages.getStringList("Commands.Nickname.Nickname-In-Use"))
					sender.sendMessage(Utils.translateColor(index));
				return;
			} catch (LengthException e) {
				switch (e.getType()) {
				case TOO_BIG:
					for (String index : messages.getStringList("Commands.Nickname.Nickname-Too-Big")) {

						MessageBuilder msg = new MessageBuilder(index);
						msg.replace("{SIZE}", String.valueOf(e.getLength()), true);
						msg.replace("${NICKNAME}", args[1], false);
						msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Nickname.Maximum-Length")), true);
						msg.replace("${MINSIZE}", String.valueOf(config.getInt("Nickname.Minimum-Length")), true);

						sender.sendMessage(msg.build());
					}
					return;
				case TOO_SMALL:
					for (String index : messages.getStringList("Commands.Nickname.Nickname-Too-Small")) {

						MessageBuilder msg = new MessageBuilder(index);
						msg.replace("{SIZE}", String.valueOf(e.getLength()), true);
						msg.replace("${NICKNAME}", args[1], false);
						msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Nickname.Maximum-Length")), true);
						msg.replace("${MINSIZE}", String.valueOf(config.getInt("Nickname.Minimum-Length")), true);

						sender.sendMessage(msg.build());
					}
					return;
				}
			} catch (NicknameCannotBePlayerNameException e) {

				if (!sender.getName().equalsIgnoreCase(Utils.stripColorCodes(e.getNickname()))) {

					for (String index : messages.getStringList("Commands.Nickname.Nickname-Cannot-Be-Player-Name"))
						sender.sendMessage(Utils.translateColor(index));
					return;
				}
			}

			target.getMeta().setNickname(args[1]);

			for (String index : messages.getStringList("Commands.Nickname.Other.Updated"))
				sender.sendMessage(new TextComponent(
						Utils.translateColor(index.replace("${NICKNAME}", args[1]).replace("${PLAYER}", args[0]))));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
