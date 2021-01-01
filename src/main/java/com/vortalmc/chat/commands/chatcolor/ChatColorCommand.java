package com.vortalmc.chat.commands.chatcolor;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenColorException;
import com.vortalmc.chat.users.meta.exceptions.InvalidColorException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class ChatColorCommand extends CommandListener {

	public ChatColorCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ChatColor.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("ChatColor.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("ChatColor.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		
		if(!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));
			
			return;
		}
		
		if(args.length == 0) {
			
			for(String index : messages.getStringList("Commands.Chat-Color.Usage"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}
		
		if(args.length > 1) {
			
			// Dispatch chat color other cmd
			
		}
		
		if(args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("off")) {
			
			User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setChatColor(config.getString("Defaults.Chat-Color"));
			
			for(String index : messages.getStringList("Commands.Chat-Color.Removed"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}
		
		try {
			
			VortalMCChat.getInstance().getMetaValidator().validateChatColor(args[0]);
			
		} catch (ForbiddenColorException | InvalidColorException e) {
			
			for(String index : messages.getStringList("Commands.Chat-Color.Forbidden-Color")) {
				
				String color = (e instanceof ForbiddenColorException) ? ((ForbiddenColorException) e).getForbiddenColor() : ((InvalidColorException) e).getForbiddenColor();
				
				MessageBuilder msg = new MessageBuilder(index);	
				msg.replace("${COLOR}", color, false);
				
				sender.sendMessage(msg.build());
			}
			return;
		}
		
		User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setChatColor(args[0]);
		
		for(String index : messages.getStringList("Commands.Chat-Color.Updated"))
			sender.sendMessage(Utils.translateColor(index.replace("${COLOR}", String.valueOf(ChatColor.getByChar(args[0].toCharArray()[1])))));
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}

}
