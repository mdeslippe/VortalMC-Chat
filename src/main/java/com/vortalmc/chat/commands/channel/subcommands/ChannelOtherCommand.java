package com.vortalmc.chat.commands.channel.subcommands;

import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

public class ChannelOtherCommand extends CommandListener {

	public ChannelOtherCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Channel.Subcommands.Other.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Channel.Subcommands.Other.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Channel.Subcommands.Other.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
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
			return;
		}
		
		Iterator<Entry<String, Channel>> channels = VortalMCChat.getInstance().getChannelManager().getChannels().entrySet().iterator();

		while (channels.hasNext()) {

			Entry<String, Channel> index = channels.next();

			if (args[1].equalsIgnoreCase(index.getKey())) {

				target.setChatChannel(index.getKey());

				for (String msgIndex : messages.getStringList("Commands.Channel.Other.Switched")) {
					MessageBuilder msg = new MessageBuilder(msgIndex);
					msg.replace("${CHANNEL}", index.getKey(), true);
					msg.replace("${PLAYER}", args[0], true);
					sender.sendMessage(msg.build());
				}

				return;
			}
		}

		for (String index : messages.getStringList("Commands.Channel.Invalid-Channel")) {

			MessageBuilder msg = new MessageBuilder(index);
			msg.replace("${PLAYER}", args[0], true);
			msg.replace("${CHANNEL}", args[1], true);
			sender.sendMessage(msg.build());
		}
	}
	
	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
