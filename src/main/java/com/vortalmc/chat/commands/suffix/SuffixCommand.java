package com.vortalmc.chat.commands.suffix;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.suffix.subcommands.SuffixOtherCommand;
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
 * The VortalMC-Chat suffix command.
 * 
 * <p>
 * This class is specifically dedicated to changing the suffix of the executor.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class SuffixCommand extends CommandListener {

	public SuffixCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Suffix.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Suffix.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Suffix.Aliases").toArray(new String[0])
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

			for (String index : messages.getStringList("Commands.Suffix.Usage"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		// Check if the sender is trying to change another players suffix.
		if (args.length > 1) {

			SuffixOtherCommand cmd = new SuffixOtherCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);
			
			return;
		}

		// Check if the sender is trying to remove their suffix.
		if (args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("off")) {

			User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setSuffix("none");

			for (String index : messages.getStringList("Commands.Suffix.Removed"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		// Validate the suffix.
		try {
			
			VortalMCChat.getInstance().getMetaValidator().validateSuffix(args[0]);
			
		} catch (ForbiddenTextException e) {

			for (String index : messages.getStringList("Commands.Suffix.Forbidden-Character")) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${CHARACTER}", e.getForbiddenText(), false);

				sender.sendMessage(msg.build());
			}

			return;
			
		} catch (LengthException e) {

			String path = "Commands.Suffix.";

			switch (e.getLengthExceptionType()) {
			case TOO_BIG:
				path += "Suffix-Too-Big";
				break;
			case TOO_SMALL:
				path += "Suffix-Too-Small";
				break;
			}

			for (String index : messages.getStringList(path)) {

				MessageBuilder msg = new MessageBuilder(index);
				msg.replace("${SUFFIX}", args[0], false);
				msg.replace("${MINSIZE}", String.valueOf(config.getInt("Suffix.Minimum-Length")), false);
				msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Suffix.Maximum-Length")), false);

				sender.sendMessage(msg.build());
			}

			return;
		}

		// Update the validated suffix.
		User.fromProxiedPlayer((ProxiedPlayer) sender).getMeta().setSuffix(args[0]);

		for (String index : messages.getStringList("Commands.Suffix.Updated")) {

			MessageBuilder msg = new MessageBuilder(index);
			msg.replace("${SUFFIX}", args[0], sender.hasPermission(permissions.getString("Color.Suffix")));

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
