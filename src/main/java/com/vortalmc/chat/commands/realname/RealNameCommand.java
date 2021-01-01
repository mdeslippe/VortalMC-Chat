package com.vortalmc.chat.commands.realname;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat realname command.
 * 
 * <p>
 * This command is used to get the minecraft account name of players with 
 * nicknames.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class RealNameCommand extends CommandListener {

	public RealNameCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Realname.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Realname.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Realname.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if the user specified enough arguments.
		if (args.length == 0) {
			
			for (String index : messages.getStringList("Commands.Realname.Usage"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		User user = User.fromPartialNickname(args[0]);

		// Send the players real name if they exist.
		if (user == null)
			for (String index : messages.getStringList("Commands.Realname.Player-Not-Found"))
				sender.sendMessage(Utils.translateColor(index.replace("${NICKNAME}", args[0])));
		else
			for (String str : messages.getStringList("Commands.Realname.Format"))
				sender.sendMessage(Utils.translateColor(str.replace("${NICKNAME}", user.getMeta().getNickname()).replace("${PLAYER}", user.getAsProxiedPlayer().getName())));
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
