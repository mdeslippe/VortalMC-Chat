package com.vortalmc.chat.commands.message;

import java.util.Arrays;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.command.CommandListener;
import com.vortalmc.chat.utils.event.defined.MessageSentEvent;
import com.vortalmc.chat.utils.message.MessageBuilder;

import net.md_5.bungee.api.CommandSender;
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
		Configuration permissions = VortalMCChat.getInstance().getFileManager().getFile("permissions").getConfiguration();

		// Ensure the sender is a ProxiedPlayer.
		if (!(sender instanceof ProxiedPlayer)) {
			
			sender.sendMessage(new TextComponent("Error: You must be a player to execute this command!"));
			
			return;
		}

		// Ensure enough aruguments were specified.
		if (args.length < 2) {
			
			for (String index : messages.getStringList("Commands.Message.Usage"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}

		// Define variables.
		User messager = User.fromProxiedPlayer((ProxiedPlayer) sender);
		User receiver = User.fromPartialName(Utils.stripColorCodes(args[0]));
		String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

		// Ensure the target player exists.
		if (receiver == null || Utils.stripColorCodes(args[0]).equalsIgnoreCase("none")) {
			
			for (String index : messages.getStringList("Commands.Message.Player-Not-Found"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", Utils.stripColorCodes(args[0]))));
			
			return;
		}
		
		// Check if the player sending the message has messages enabled.
		if(!messager.hasMessagesEnabled()) {
			
			for(String index : messages.getStringList("Commands.Message.Sender-Has-Messages-Disabled"))
				sender.sendMessage(Utils.translateColor(index));
			
			return;
		}
		
		// Check if the player receiving the message has messages enabled.
		if(!receiver.hasMessagesEnabled()) {
			
			for(String index : messages.getStringList("Commands.Message.Receiver-Has-Messages-Disabled"))
				sender.sendMessage(Utils.translateColor(index.replace("${PLAYER}", args[0])));
			
			return;
		}
		
		// Send the message to the sender.
		for (String index : messages.getStringList("Commands.Message.Send-Format")) {

			MessageBuilder msg = new MessageBuilder(index);

			msg.replace("${SENDER}", messager.getMeta().getPreferredName(), true);
			msg.replace("${RECEIVER}", receiver.getMeta().getPreferredName(), true);
			msg.replace("${MESSAGE}", message, sender.hasPermission(permissions.getString("Color.Text")));

			messager.getAsProxiedPlayer().sendMessage(msg.build());
		}

		// Send the message to the receiver.
		for (String index : messages.getStringList("Commands.Message.Receive-Format")) {

			MessageBuilder msg = new MessageBuilder(index);

			msg.replace("${SENDER}", messager.getMeta().getPreferredName(), true);
			msg.replace("${RECEIVER}", receiver.getMeta().getPreferredName(), true);
			msg.replace("${MESSAGE}", message, sender.hasPermission(permissions.getString("Color.Text")));
			
			receiver.getAsProxiedPlayer().sendMessage(msg.build());
		}

		// Update the player data.
		messager.setLastMessageReceiver(receiver.getUUID());
		receiver.setLastMessageSender(messager.getUUID());
		
		// Dispatch the internal MessageSentEvent.
		VortalMCChat.getInstance().getInternalEventManager().dispatchEvent(new MessageSentEvent(messager.getAsProxiedPlayer(), receiver.getAsProxiedPlayer(), message));
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}
	
}
