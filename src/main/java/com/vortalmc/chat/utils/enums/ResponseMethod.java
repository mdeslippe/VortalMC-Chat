package com.vortalmc.chat.utils.enums;

/**
 * The response methods for the
 * {@link com.vortalmc.chat.commands.message.RespondCommand RespondCommand}.
 * 
 * @author Myles Deslippe
 */
public enum ResponseMethod {

	/**
	 * Send the response to the player that last messaged the sender.
	 */
	LAST_MESSAGE_SENDER,

	/**
	 * SEnd the response to the player that the sender last messaged.
	 */
	LAST_MESSAGE_RECEIVER;

}
