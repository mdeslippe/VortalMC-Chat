package com.vortalmc.chat.events.bungee.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.utils.Utils;

import litebans.api.Database;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

/**
 * This class will be used to manage all player chat events.
 * 
 * @author Myles Deslippe
 */
public class PlayerChatEvent implements Listener {

	/**
	 * This method will handle the chat event.
	 * 
	 * @param event The corresponding event.
	 */
	@EventHandler
	public void onChatEvent(ChatEvent event) {

		// Ensure the message is not a command.
		if (event.getMessage().startsWith("/"))
			return;

		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		if (Database.get().isPlayerMuted(player.getUniqueId(), player.getSocketAddress().toString()))
			this.displayMuteMessage(player);
		else
			VortalMCChat.getInstance().dispatchMessage(player, event.getMessage());

		event.setCancelled(true);
	}

	/**
	 * Display the muted message.
	 * 
	 * @param player The player to display the message to.
	 */
	private void displayMuteMessage(ProxiedPlayer player) {

		Configuration messages = VortalMCChat.getInstance().getFileManager().getFile("messages").getConfiguration();

		try {
			PreparedStatement statement = Database.get()
					.prepareStatement("SELECT * FROM {mutes} WHERE `uuid` = ? AND `active` = ?");
			statement.setString(1, player.getUniqueId().toString());
			statement.setBoolean(2, true);

			ResultSet results = statement.executeQuery();

			if (results.next()) {

				String time_remaining = Utils.timeStampToString(results.getLong("until") - System.currentTimeMillis());
				String ban_reason = results.getString("reason");
				String ban_by = ProxyServer.getInstance()
						.getPlayer(UUID.fromString(results.getString("banned_by_uuid"))).getName();

				for (String index : messages.getStringList("Events.Chat.Mute-Message")) {

					index.replace("${TIME_REMAINING}", time_remaining);
					index.replace("${BAN_REASON}", ban_reason);
					index.replace("${BANED_BY}", ban_by);

					player.sendMessage(new TextComponent(Utils.translateColor(index)));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
