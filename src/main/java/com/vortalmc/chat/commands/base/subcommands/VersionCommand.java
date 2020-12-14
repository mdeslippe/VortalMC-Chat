package com.vortalmc.chat.commands.base.subcommands;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat version command.
 * 
 * @author Myles Deslippe
 */
public class VersionCommand extends CommandListener {

	public VersionCommand() {
		super(
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("VortalMCChat.Subcommands.Version.Name"),
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("VortalMCChat.Subcommands.Version.Permission"), 
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("VortalMCChat.Subcommands.Version.Aliases").toArray(new String[0])
		);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Commands.VortalMC-Chat.Base-Command.Sub-Commands.Version-Command.Message"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${VERSION}", VortalMCChat.getInstance().getDescription().getVersion()))));
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
