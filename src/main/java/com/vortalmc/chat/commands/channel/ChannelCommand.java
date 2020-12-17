package com.vortalmc.chat.commands.channel;

import java.util.Iterator;
import java.util.Map.Entry;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.channel.subcommands.ChannelOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

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

		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));
		}

		User user = User.fromProxiedPlayer((ProxiedPlayer) sender);

		if (args.length == 0) {
			for (String index : messages.getStringList("Commands.Channel.Usage")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${CURRENT_CHANNEL}", user.getChatChannel(), true);
				sender.sendMessage(msg.build());

			}
		} else if (args.length == 1) {

			Iterator<Entry<String, Channel>> channels = VortalMCChat.getInstance().getChannelManager().getChannels().entrySet().iterator();

			while (channels.hasNext()) {

				Entry<String, Channel> index = channels.next();

				if (args[0].equalsIgnoreCase(index.getKey())) {

					user.setChatChannel(index.getKey());

					for (String msgIndex : messages.getStringList("Commands.Channel.Switched")) {
						MessageBuilder msg = new MessageBuilder(msgIndex);
						msg.replace("${CHANNEL}", index.getKey(), true);
						sender.sendMessage(msg.build());
					}

					return;
				}
			}

			for (String index : messages.getStringList("Commands.Channel.Invalid-Channel")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${CHANNEL}", args[0], true);
				sender.sendMessage(msg.build());
			}

		} else {

			ChannelOtherCommand cmd = new ChannelOtherCommand();
			
			if(sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);
			
		}

	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}

}
