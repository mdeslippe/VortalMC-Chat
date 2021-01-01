package com.vortalmc.chat.commands.prefix;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.prefix.subcommands.PrefixOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
import com.vortalmc.chat.users.meta.exceptions.LengthException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat prefix command.
 * 
 * <p>
 * This class is specifically dedicated to changing the prefix of the executor.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class PrefixCommand extends CommandListener {

	public PrefixCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Prefix.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Prefix.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Prefix.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		Configuration permissions = VortalMCChat.getInstance().getFileManager().getFile("permissions").getConfiguration();

		// Check if the sender is a player.
		if (!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to use this command!"));
			
			return;
		}
		
		// Check if the sender specified enough arguments.
		if (args.length == 0) {

			for (String index : messages.getStringList("Commands.Prefix.Usage"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		// Check if the sender is trying to change another players prefix.
		if (args.length > 1) {

			PrefixOtherCommand cmd = new PrefixOtherCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);
			
			return;
		}

		// Check if the sender is trying to remove their prefix.
		if (args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("off")) {

			User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setPrefix("none");

			for (String index : messages.getStringList("Commands.Prefix.Removed"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		// Validate the prefix.
		try {
			
			VortalMCChat.getInstance().getMetaValidator().validatePrefix(args[0]);
			
		} catch (ForbiddenTextException e) {

			for (String index : messages.getStringList("Commands.Prefix.Forbidden-Character")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${CHARACTER}", e.getForbiddenText(), false);

				sender.sendMessage(msg.build());
			}

			return;
			
		} catch (LengthException e) {

			String path = "Commands.Prefix.";

			switch (e.getLengthExceptionType()) {
			case TOO_BIG:
				path += "Prefix-Too-Big";
				break;
			case TOO_SMALL:
				path += "Prefix-Too-Small";
				break;
			}

			for (String index : messages.getStringList(path)) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${PREFIX}", args[0], false);
				msg.replace("${MINSIZE}", String.valueOf(config.getInt("Prefix.Minimum-Length")), false);
				msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Prefix.Maximum-Length")), false);

				sender.sendMessage(msg.build());
			}

			return;
		}

		// Update the validated prefix.
		User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setPrefix(args[0]);

		for (String index : messages.getStringList("Commands.Prefix.Updated")) {

			MessageBuilder msg = new MessageBuilder(index);
			msg.replace("${PREFIX}", args[0], sender.hasPermission(permissions.getString("Color.Prefix")));

			sender.sendMessage(msg.build());
		}

	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
