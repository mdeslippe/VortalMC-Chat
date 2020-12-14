package com.vortalmc.chat.commands.base;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.base.subcommands.HelpCommand;
import com.vortalmc.chat.commands.base.subcommands.ReloadCommand;
import com.vortalmc.chat.commands.base.subcommands.VersionCommand;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat base command.
 * 
 * @author Myles Deslippe
 */
public class VortalMCChatCommand extends CommandListener {
	
	public VortalMCChatCommand() {
		super(
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("VortalMCChat.Name"),
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("VortalMCChat.Permission"), 
		VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("VortalMCChat.Aliases").toArray(new String[0])
		);
		this.addSubCommandListener(new HelpCommand());
		this.addSubCommandListener(new VersionCommand());
		this.addSubCommandListener(new ReloadCommand());
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Commands.VortalMC-Chat.Base-Command.Message")) {
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		
		for(String index : messages.getStringList("Error.Permission-Denied")) {
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
		}
	}
}
