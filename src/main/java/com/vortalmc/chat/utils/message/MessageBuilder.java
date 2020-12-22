package com.vortalmc.chat.utils.message;

import java.util.Vector;

import com.vortalmc.chat.utils.Utils;

import net.md_5.bungee.api.chat.TextComponent;

/**
 * A message building utility.
 * 
 * @author Myles Deslippe
 */
public class MessageBuilder {

	/**
	 * The message that is going to be formatted.
	 */
	private String message;

	/**
	 * Create a new MessageBuilder.
	 */
	public MessageBuilder() {
		this.message = "";
	}

	/**
	 * Create a new MessageBuilder.
	 * 
	 * @param message The message that is going to be formatted.
	 */
	public MessageBuilder(String message) {
		this.message = message;
	}

	/**
	 * Append the message that is being built.
	 * 
	 * @param message The message to add on.
	 */
	public void append(String message) {
		this.message += message;
	}

	/**
	 * Replace all occurrences of a string.
	 * 
	 * <p>
	 * <strong>Note:</strong> If color translation is irrelevent (for example, a
	 * placeholder is being replace by an integer), set the color paramater to false
	 * for a slight performance gain.
	 * </p>
	 * 
	 * @param target The target string to replace.
	 * @param value  The value to replace the target.
	 * @param color  If the replacement is going to be color formatted.
	 */
	public void replace(String target, Object value, boolean color) {

		if (color)
			message = message.replace(target, String.valueOf(value));
		else
			message = message.replace(target, String.valueOf(value).replace("&", "\\&"));
	}

	/**
	 * Build the message.
	 * 
	 * @return The built message.
	 */
	public TextComponent build() {

		TextComponent buffer = new TextComponent("");
		Vector<Integer> locations = new Vector<Integer>();
		int startPointer = 0, endPointer = 0;
		String msgCopy = message;

		while (msgCopy.contains("\\&")) {
			locations.add(msgCopy.indexOf("\\&"));
			msgCopy = msgCopy.replaceFirst("[\\\\][&]", "&");
		}

		try {
			for (int i = 0; locations.get(i) != null; i++) {

				// The end pointer is one index past the part we want to copy because of the
				// weird way substring works.
				endPointer = locations.get(i);

				if (startPointer != endPointer) {
					buffer.addExtra(Utils.translateColor(msgCopy.substring(startPointer, endPointer)));
					startPointer = endPointer;
				}

				endPointer += 2;

				buffer.addExtra(new TextComponent(msgCopy.substring(startPointer, endPointer)));

				startPointer = endPointer;
			}

			// This exception will be thrown when the loop attemtps to check if
			// location.get(i) is null, it can be ignored.
		} catch (ArrayIndexOutOfBoundsException ex) {
		}

		if (endPointer != msgCopy.length())
			buffer.addExtra(Utils.translateColor(msgCopy.substring(endPointer, msgCopy.length())));
		return buffer;
	}
}
