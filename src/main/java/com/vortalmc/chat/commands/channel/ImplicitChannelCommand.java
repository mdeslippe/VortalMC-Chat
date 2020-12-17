package com.vortalmc.chat.commands.channel;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ImplicitChannelCommand extends CommandListener {

	private Channel channel;

	public ImplicitChannelCommand(Channel channel) {
		super(channel.getName(), channel.getPermission(), channel.getAliases());
		this.channel = channel;
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("Error: You must be a player to execute this command!"));
		}

		if (args.length == 0) {

			User.fromProxiedPlayer((ProxiedPlayer) sender).setChatChannel(channel.getName());

			for (String msgIndex : messages.getStringList("Commands.Channel.Switched")) {
				MessageBuilder msg = new MessageBuilder(msgIndex);
				msg.replace("${CHANNEL}", channel.getName(), true);
				sender.sendMessage(msg.build());
			}
			
			return;
		} 
			
		String message = String.join(" ", args);
		VortalMCChat.getInstance().dispatchMessage((ProxiedPlayer) sender, message, this.channel);
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}

}
