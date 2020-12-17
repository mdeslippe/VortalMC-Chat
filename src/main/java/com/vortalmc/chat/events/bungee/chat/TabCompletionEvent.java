package com.vortalmc.chat.events.bungee.chat;

import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;

import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class TabCompletionEvent implements Listener {

	@EventHandler
	public void onTabComplete(TabCompleteEvent event) {

		String[] curser = event.getCursor().split(" ");
		int lastIndex = curser.length - 1;

		User user = User.fromPartialName(curser[lastIndex]);

		if (user != null) {

			// Check if the user is typing the players nickname or username
			if (user.getMeta().hasNickname() && user.getMeta().getNickname().toLowerCase().contains(curser[lastIndex].toLowerCase()))
				event.getSuggestions().add(Utils.stripColorCodes(user.getMeta().getNickname()));
			else
				event.getSuggestions().add(user.getAsProxiedPlayer().getName());
		}
	}
}
