package com.vortalmc.chat.utils;

import com.vortalmc.chat.utils.time.TimeUnit;

import net.md_5.bungee.api.ChatColor;

/**
 * A general utilities class.
 * 
 * @author Myles Deslippe
 */
public class Utils {

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

	/**
	 * Turn a timestamp into its string representation.
	 * 
	 * @param stamp The timestamp to convert.
	 * 
	 * @return The string representation of the timestamp.
	 */
	public static String timeStampToString(long stamp) {

		String str = "";
		long buffer;

		buffer = millisecondsToUnit(stamp, TimeUnit.DECADES);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "de");

		buffer = millisecondsToUnit(stamp, TimeUnit.YEARS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "yr");

		buffer = millisecondsToUnit(stamp, TimeUnit.MONTHS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "mo");

		buffer = millisecondsToUnit(stamp, TimeUnit.WEEKS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "w");

		buffer = millisecondsToUnit(stamp, TimeUnit.DAYS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "d");

		buffer = millisecondsToUnit(stamp, TimeUnit.HOURS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "h");

		buffer = millisecondsToUnit(stamp, TimeUnit.MINUTES);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "m");

		buffer = millisecondsToUnit(stamp, TimeUnit.SECONDS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "s");

		return str;
	}

	/**
	 * Appent a time string.
	 */
	private static String appendTimeFormat(String format, String str) {
		if (format != "")
			return " " + format;
		else
			return format;
	}

	/**
	 * Convert milliseconds to other time units.
	 * 
	 * @param ms   The amount of time in milliseconds.
	 * @param unit The time unit to convert to.
	 * 
	 * @return The converted time.
	 */
	public static long millisecondsToUnit(long ms, TimeUnit unit) {
		switch (unit) {
		case SECONDS:
			return ms / 1000L;
		case MINUTES:
			return ms / 60000L;
		case HOURS:
			return ms / 3600000L;
		case DAYS:
			return ms / 86400000L;
		case WEEKS:
			return ms / 604800000L;
		case MONTHS:
			return ms / 2629746000L;
		case YEARS:
			return ms / 31536000000L;
		case DECADES:
			return ms / 315360000000L;
		default:
			return -1;
		}
	}
}
