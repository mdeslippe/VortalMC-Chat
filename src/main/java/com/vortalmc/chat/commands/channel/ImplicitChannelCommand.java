package com.vortalmc.chat.commands.channel;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.command.CommandListener;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;

/**
 * The ImplicitChannelCommand.
 * 
 * <p>
 * These channel commands will be dynamically registered based on the channels
 * define in the config.yml configuration file.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class ImplicitChannelCommand extends CommandListener {

	/**
	 * The channel that is being registered.
	 */
	private Channel channel;

	/**
	 * The ImplicitChannelCommand constructor.
	 * 
	 * @param channel The channel that is being registered.
	 */
	public ImplicitChannelCommand(Channel channel) {
		super(channel.getName(), channel.getPermission(), channel.getAliases());
		this.channel = channel;
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		// Check to see if the sender is a player.
		if (!(sender instanceof ProxiedPlayer)) {

			sender.sendMessage(new TextComponent("Error: You must be a player to execute this command!"));

			return;
		}

		// Change the sender's channel if no arguments were specified.
		if (args.length == 0) {

			User.fromProxiedPlayer((ProxiedPlayer) sender).setChatChannel(channel.getName());

			for (String index : messages.getStringList("Commands.Channel.Switched"))
				sender.sendMessage(Utils.translateColor(index.replace("${CHANNEL}", channel.getName())));

			return;
		}

		// Check to make sure the player has chat enabled.
		if (!User.fromProxiedPlayer((ProxiedPlayer) sender).hasChatEnabled()) {

			for (String index : messages.getStringList("Error.Chat-Not-Enabled"))
				sender.sendMessage(Utils.translateColor(index));

			return;
		}

		// Dispatch the ChatEvent with the ProxyServer.
		ChatEvent event = ProxyServer.getInstance().getPluginManager().callEvent(new ChatEvent((ProxiedPlayer) sender, null, String.join(" ", args)));

		// Send the sender's message if it has not be cancelled (for example by Litebans
		// because the player is muted).
		if (!event.isCancelled())
			VortalMCChat.getInstance().dispatchMessage((ProxiedPlayer) sender, String.join(" ", args), this.channel);
	}

	@Override
	public void onPermissionDenied(CommandSender sender, String[] args) {
		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		for (String index : messages.getStringList("Error.Permission-Denied"))
			sender.sendMessage(Utils.translateColor(index));
	}

}
