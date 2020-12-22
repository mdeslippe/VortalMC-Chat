package com.vortalmc.chat.commands.socialspy;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.socialspy.subcommands.SocialSpyOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat social spy command.
 * 
 * <p>
 * This class is specifically dedicated to toggling socialspy for the executor.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class SocialSpyCommand extends CommandListener {

	public SocialSpyCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("SocialSpy.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("SocialSpy.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("SocialSpy.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check to make sure the sender is a player.
		if (!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));
			
			return;
		}

		// Check to see if the sender is trying to toggle social spy for another player.
		if (args.length > 0) {
			
			SocialSpyOtherCommand cmd = new SocialSpyOtherCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);

			return;
		}

		// Get the User's data.
		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);

		// Toggle the User's social spy.
		if (user.hasSocialSpyEnabled()) {
			
			user.disableSocialSpy();

			for (String index : messages.getStringList("Commands.SocialSpy.Disabled"))
				sender.sendMessage(Utils.translateColor(index));

		} else {
			
			user.enableSocialSpy();

			for (String index : messages.getStringList("Commands.SocialSpy.Enabled"))
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
