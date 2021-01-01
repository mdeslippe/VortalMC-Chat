package com.vortalmc.chat.commands.channel;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.channel.subcommands.ChannelOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat channel command.
 * 
 * <p>
 * This class is specifically dedicated for changing the executors channel.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class ChannelCommand extends CommandListener {

	public ChannelCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Channel.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Channel.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Channel.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		// Check to make sure it is a player attempting to send the message.
		if (!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));
			
			return;
		}

		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);

		// Check to see if a channel was specified.
		if (args.length == 0) {
			
			for (String index : messages.getStringList("Commands.Channel.Usage"))
				sender.sendMessage(Utils.translateColor(index.replace("${CURRENT_CHANNEL}", user.getChatChannel())));
	
			return;
		}
		
		// If the player is trying to change another player's channel.
		if (args.length > 1) {
			ChannelOtherCommand cmd = new ChannelOtherCommand();
			
			if(sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);
			
			return;
		}
		
		// If the player is trying to change their own channel.
		if (args.length == 1) {
			
			// Check to see if the channel specified was a valid channel.
			if (VortalMCChat.getInstance().getChannelManager().containsChannel(args[0])) {

				Channel channel = VortalMCChat.getInstance().getChannelManager().getChannel(args[0]);
				user.setChatChannel(channel.getName());

				for (String msgIndex : messages.getStringList("Commands.Channel.Switched")) 
					sender.sendMessage(Utils.translateColor(msgIndex.replace("${CHANNEL}", channel.getName())));
				
				return;
			}
			
			// If the channel was invalid.
			for (String index : messages.getStringList("Commands.Channel.Invalid-Channel"))
				sender.sendMessage(Utils.translateColor(index.replace("${CHANNEL}", args[0])));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
