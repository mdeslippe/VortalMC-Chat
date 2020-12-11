package com.vortalmc.chat.utils;

import com.vortalmc.chat.VortalMCChat;

import net.md_5.bungee.api.ChatColor;

/**
 * A general utilities class.
 * 
 * @author Myles Deslippe
 */
public class Utils {

	/**
	 * Log a message to the console.
	 * 
	 * @param message The message to log.
	 */
	public static void log(final String message) {
		VortalMCChat.getInstance().getLogger().info(message);
	}

	/**
	 * Log a warning to the console.
	 * 
	 * @param message The warning to log.
	 */
	public static void warn(final String message) {
		VortalMCChat.getInstance().getLogger().warning(message);
	}

	/**
	 * Translate minecraft color codes.
	 * 
	 * @param message The raw String to translate.
	 * 
	 * @return The translated String.
	 */
	public static String translateColor(final String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	/**
	 * Check if an array contains an element.
	 * 
	 * @param array   The array to check.
	 * @param element The element to check for.
	 * @return The truth value associated with the array containing the element.
	 */
	public static boolean checkIfArrayContainsElement(Object[] array, Object element) {
		for (Object index : array)
			if (index == element)
				return true;

		return false;
	}
}
