package com.vortalmc.chat.commands.toggle;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat toggle-chat command.
 * 
 * @author Myles Deslippe
 */
public class ToggleChatCommand extends CommandListener {

	public ToggleChatCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ToggleChat.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ToggleChat.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("ToggleChat.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if it is a player trying to toggle their chat.
		if (!(sender instanceof ProxiedPlayer)) {

			sender.sendMessage(Utils.translateColor("Error: You must be a player to use this command!"));

			return;
		}

		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);
		String path;

		// Toggle the player's chat.
		if (user.hasChatEnabled()) {

			user.disableChat();
			path = "Chat-Disabled";

		} else {

			user.enableChat();
			path = "Chat-Enabled";

		}

		// Send the chat toggled message.
		for (String index : messages.getStringList("Commands.ToggleChat." + path))
			sender.sendMessage(Utils.translateColor(index));

	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}

}