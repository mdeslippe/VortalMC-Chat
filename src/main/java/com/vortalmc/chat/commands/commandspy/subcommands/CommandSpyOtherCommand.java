package com.vortalmc.chat.commands.commandspy.subcommands;

import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

public class CommandSpyOtherCommand extends CommandListener {

	public CommandSpyOtherCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("CommandSpy.Other.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("CommandSpy.Other.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("CommandSpy.Other.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		String response = Utils.getMojangPlayerData(args[0]);
		
		if(response == null) {
			
			for(String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
			
			return;
		}
		
		UUID uuid = UUID.fromString(Utils.formatUUID(new Gson().fromJson(response, JsonObject.class).get("id").getAsString()));
		User target = User.fromUUID(uuid);
		
		if (!target.isInDatabase()) {
			for(String index : messages.getStringList("Error.Player-Does-Not-Exist"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
			
			return;
		}
		
		if(target.hasCommandSpyEnabled()) {
			
			target.disableCommandSpy();
			
			for(String index : messages.getStringList("Commands.CommandSpy.Other.Disabled"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));
			
		} else {
			
			target.enableCommandSpy();
			
			for(String index : messages.getStringList("Commands.CommandSpy.Other.Enabled"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));	
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}

}