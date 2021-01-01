package com.vortalmc.chat.commands.message;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat toggle-message command.
 * 
 * @author Myles Deslippe
 */
public class ToggleMessageCommand extends CommandListener {

	public ToggleMessageCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ToggleMessage.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ToggleMessage.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("ToggleMessage.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if it is a player trying to toggle their messaging.
		if (!(sender instanceof ProxiedPlayer)) {

			sender.sendMessage(Utils.translateColor("Error: You must be a player to use this command!"));

			return;
		}

		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);
		String path;

		// Toggle the player's messaging.
		if (user.hasMessagesEnabled()) {

			user.disableMessages();
			path = "Messages-Disabled";

		} else {

			user.enableMessages();
			path = "Messages-Enabled";

		}

		// Send the messaging toggled message.
		for (String index : messages.getStringList("Commands.ToggleMessage." + path))
			sender.sendMessage(Utils.translateColor(index));

	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}

}
