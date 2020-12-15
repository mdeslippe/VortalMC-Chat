package com.vortalmc.chat.commands.message;

import java.util.Arrays;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.event.defined.MessageSentEvent;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat message command.
 * 
 * @author Myles Deslippe
 */
public class MessageCommand extends CommandListener {

	public MessageCommand() {
		super(
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Message.Name"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getString("Message.Permission"),
			VortalMCChat.getInstance().getFileManager().getFile("commands").getConfiguration().getStringList("Message.Aliases").toArray(new String[0])
			);
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check if enough arguments were supplied.
		if (args.length == 0) {

			for (String index : messages.getStringList("Commands.Message.Usage"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

			// Check if enough arguments were supplied.
		} else if (args.length == 1) {

			for (String index : messages.getStringList("Commands.Message.No-Message-Entered"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index)));

			// Check if the target player is online.
		} else if (ProxyServer.getInstance().getPlayer(args[0]) == null) {

			for (String index : messages.getStringList("Commands.Message.Player-Not-Found"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index.replace("${PLAYER}", args[0]))));

			// Send the message.
		} else {

			ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
			User sendingUser = User.fromProxiedPlayer((ProxiedPlayer) sender);
			User targetUser = User.fromProxiedPlayer(target);

			String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

			// Send the sender the message
			for (String index : messages.getStringList("Commands.Message.Send-Format"))
				sender.sendMessage(new TextComponent(Utils.translateColor(index
						.replace("${SENDER}", sender.getName())
						.replace("${RECEIVER}", target.getName())
						.replace("${MESSAGE}", message))
						));
			
			// Send the target the message.
			for (String index : messages.getStringList("Commands.Message.Receive-Format"))
				target.sendMessage(new TextComponent(Utils.translateColor(index
						.replace("${SENDER}", sender.getName())
						.replace("${RECEIVER}", target.getName())
						.replace("${MESSAGE}", message))
						));
			
				sendingUser.setLastMessageReceiver(target.getUniqueId());
				targetUser.setLastMessageSender(((ProxiedPlayer) sender).getUniqueId());

			VortalMCChat.getInstance().getInternalEventManager().dispatchEvent(new MessageSentEvent((ProxiedPlayer) sender, target, message));
		}
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(new TextComponent(Utils.translateColor(index)));
	}

}
