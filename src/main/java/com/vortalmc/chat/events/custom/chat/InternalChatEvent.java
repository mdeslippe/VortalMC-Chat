package com.vortalmc.chat.events.custom.chat;

import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.event.ChatEvent;

/**
 * The InternalChatEvent.
 * 
 * <p>
 * This event is used to run the event through the typical validation that
 * regular {@link net.md_5.bungee.api.event.ChatEvent ChatEvent}'s undergo (for
 * example cancelling the event if the sender is muted), without dispatching the
 * message, allowing for the message to be dispatched manually elsewhere.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class InternalChatEvent extends ChatEvent {

	/**
	 * The InternalChatEvent constructor.
	 * 
	 * @param sender   The sender that executed the event.
	 * @param receiver The receiver that will be receiving the event.
	 * @param message  The message that was sent.
	 */
	public InternalChatEvent(Connection sender, Connection receiver, String message) {
		super(sender, receiver, message);
	}

}
