package com.vortalmc.chat.commands.commandspy;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.commandspy.subcommands.CommandSpyOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat command spy command.
 * 
 * <p>
 * This class is specifically dedicated to toggling commandspy for the executor.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class CommandSpyCommand extends CommandListener {

	public CommandSpyCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("CommandSpy.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("CommandSpy.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("CommandSpy.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if the sender is a player.
		if (!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));

			return;
		}

		// Check if the sender is trying to toggle command spy for another player.
		if (args.length > 0) {
			
			CommandSpyOtherCommand cmd = new CommandSpyOtherCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);

			return;
		}

		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);

		// Toggle the players commandspy.
		if (user.hasCommandSpyEnabled()) {

			user.disableCommandSpy();

			for (String index : messages.getStringList("Commands.CommandSpy.Disabled"))
				sender.sendMessage(Utils.translateColor(index));

		} else {

			user.enableCommandSpy();

			for (String index : messages.getStringList("Commands.CommandSpy.Enabled"))
				sender.sendMessage(Utils.translateColor(index));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
