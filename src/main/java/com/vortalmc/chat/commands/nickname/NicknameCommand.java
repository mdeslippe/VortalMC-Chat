package com.vortalmc.chat.commands.nickname;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.commands.nickname.subcommands.NicknameOtherCommand;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
import com.vortalmc.chat.users.meta.exceptions.LengthException;
import com.vortalmc.chat.users.meta.exceptions.NicknameCannotBePlayerNameException;
import com.vortalmc.chat.users.meta.exceptions.NicknameInUseException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat nickname command.
 * 
 * <p>
 * This class is specifically dedicated for changing the nickname of the
 * executor.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class NicknameCommand extends CommandListener {

	public NicknameCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Nickname.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Nickname.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if the user is trying to change another player's username.
		if (args.length > 1) {
			NicknameOtherCommand cmd = new NicknameOtherCommand();

			if (sender.hasPermission(cmd.getPermission()))
				cmd.onCommand(sender, args);
			else
				cmd.onPermissionDenied(sender, args);

			// Check if the user did not specify enough arguments.
		} else if (args.length == 0) {

			for (String index : messages.getStringList("Commands.Nickname.Usage"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

			// Check if the sender is trying to change their nickname but they are not a
			// player,
		} else if (!(sender instanceof ProxiedPlayer)) {

			sender.sendMessage(new TextComponent("Error: You must be a player to execute this command!"));

			// Check if the sender is trying to remove their nickname.
		} else if (args[0].equalsIgnoreCase("none") || args[0].equalsIgnoreCase("off")) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			User user = User.fromProxiedPlayer(player);
			user.getMeta().removeNickname();

			for (String index : messages.getStringList("Commands.Nickname.Removed"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

			// Update the sender's nickname.
		} else {

			try {
				VortalMCChat.getInstance().getMetaValidator().validateNickname(args[0]);
			} catch (ForbiddenTextException e) {
				for (String msg : messages.getStringList("Commands.Nickname.Forbidden-Character")) {

					MessageBuilder buffer = new MessageBuilder(msg);
					buffer.replace("${CHARACTER}", e.getText(), false);

					sender.sendMessage(buffer.build());
				}

				return;
			} catch (NicknameInUseException e) {
				for (String index : messages.getStringList("Commands.Nickname.Nickname-In-Use"))
					sender.sendMessage(Utils.translateColor(index));
				return;
			} catch (LengthException e) {
				switch (e.getType()) {
				case TOO_BIG:
					for (String index : messages.getStringList("Commands.Nickname.Nickname-Too-Big")) {

						MessageBuilder msg = new MessageBuilder(index);
						msg.replace("{SIZE}", String.valueOf(e.getLength()), true);
						msg.replace("${NICKNAME}", args[0], false);
						msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Nickname.Maximum-Length")), true);
						msg.replace("${MINSIZE}", String.valueOf(config.getInt("Nickname.Minimum-Length")), true);

						sender.sendMessage(msg.build());
					}
					return;
				case TOO_SMALL:
					for (String index : messages.getStringList("Commands.Nickname.Nickname-Too-Small")) {

						MessageBuilder msg = new MessageBuilder(index);
						msg.replace("{SIZE}", String.valueOf(e.getLength()), true);
						msg.replace("${NICKNAME}", args[0], false);
						msg.replace("${MAXSIZE}", String.valueOf(config.getInt("Nickname.Maximum-Length")), true);
						msg.replace("${MINSIZE}", String.valueOf(config.getInt("Nickname.Minimum-Length")), true);

						sender.sendMessage(msg.build());
					}
					return;
				}
			} catch (NicknameCannotBePlayerNameException e) {

				if (!sender.getName().equalsIgnoreCase(Utils.stripColorCodes(e.getNickname()))) {

					for (String index : messages.getStringList("Commands.Nickname.Nickname-Cannot-Be-Player-Name"))
						sender.sendMessage(Utils.translateColor(index));
					return;
				}
			}

			User user = User.fromProxiedPlayer((ProxiedPlayer) sender);
			user.getMeta().setNickname(args[0]);

			for (String index : messages.getStringList("Commands.Nickname.Updated"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${NICKNAME}", args[0]))));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}
}
