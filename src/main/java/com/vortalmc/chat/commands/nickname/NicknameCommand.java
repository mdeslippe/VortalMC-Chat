package com.vortalmc.chat.commands.nickname;

import java.sql.SQLException;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.nickname.subcommands.NicknameOfOtherPlayerCommand;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat nickname command.
 * 
 * @author Myles Deslippe
 */
public class NicknameCommand extends CommandListener {

	public NicknameCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Nickname.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if the user is trying to change another player's username.
		if (args.length > 1) {
			NicknameOfOtherPlayerCommand cmd = new NicknameOfOtherPlayerCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);

		// Check if the user did not specify enough arguments.
		} else if (args.length == 0) {

			for (String index : messages.getStringList("Commands.Nickname.Usage"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

		// Check if the sender is trying to change their nickname but they are not a player,
		} else if (!(sender instanceof ProxiedPlayer)) {

			sender.sendMessage(new TextComponent("Error: You must be a player to execute this command!"));

		// Check if the sender is trying to remove their nickname.
		} else if (args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("off")) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			try {
				VortalMCChat.getInstance().updatePlayerColumn(player.getUniqueId(), "nickname", "none");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (String index : messages.getStringList("Commands.Nickname.Removed"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

		// Update the sender's nickname.
		} else {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			try {
				VortalMCChat.getInstance().updatePlayerColumn(player.getUniqueId(), "nickname", args[0]);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			for (String index : messages.getStringList("Commands.Nickname.Updated"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${NICKNAME}", args[0]))));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
