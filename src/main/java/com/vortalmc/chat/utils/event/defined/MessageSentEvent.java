package com.vortalmc.chat.utils.event.defined;

import com.vortalmc.chat.utils.event.Event;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * The message sent event.
 * 
 * <p>
 * This event will be dispatched when a use sends a message via the
 * {@link com.vortalmc.chat.commands.message.MessageCommand MessageCommand}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class MessageSentEvent implements Event {

	/**
	 * The player that sent the message.
	 */
	private final ProxiedPlayer sender;

	/**
	 * The player the received the message.
	 */
	private final ProxiedPlayer receiver;

	/**
	 * The message that was sent.
	 */
	private String message;

	/**
	 * Create a new message sent event.
	 * 
	 * @param sender   The player the sent the message.
	 * @param receiver The player that received the message.
	 * @param message  The message that was sent.
	 */
	public MessageSentEvent(ProxiedPlayer sender, ProxiedPlayer receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	/**
	 * Get the player that sent the message.
	 * 
	 * @return The player that sent the message.
	 */
	public ProxiedPlayer getSender() {
		return this.sender;
	}

	/**
	 * Get the player that received the message.
	 * 
	 * @return The player that received the message.
	 */
	public ProxiedPlayer getReceiver() {
		return this.receiver;
	}

	/**
	 * Get the message that was sent.
	 * 
	 * @return The message that was sent.
	 */
	public String getMessage() {
		return this.message;
	}

}
