package com.vortalmc.chat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.vortalmc.chat.utils.time.TimeUnit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

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
	public static BaseComponent[] translateColor(final String message) {
		return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message));
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
			appendTimeFormat(str, buffer + "decades");

		buffer = millisecondsToUnit(stamp, TimeUnit.YEARS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "years");

		buffer = millisecondsToUnit(stamp, TimeUnit.MONTHS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "months");

		buffer = millisecondsToUnit(stamp, TimeUnit.WEEKS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "weeks");

		buffer = millisecondsToUnit(stamp, TimeUnit.DAYS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "days");

		buffer = millisecondsToUnit(stamp, TimeUnit.HOURS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "hours");

		buffer = millisecondsToUnit(stamp, TimeUnit.MINUTES);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "minutes");

		buffer = millisecondsToUnit(stamp, TimeUnit.SECONDS);
		if (buffer > 0)
			appendTimeFormat(str, buffer + "seconds");

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

	/**
	 * Query playerdata from the Mojang servers.
	 * 
	 * <p>
	 * <strong>Note</strong> This will return null if the player does not exist or
	 * the servers are offline.
	 * </p>
	 * 
	 * @param name The name of the player to query.
	 * 
	 * @return The result of the query.
	 */
	public static String getMojangPlayerData(String name) {

		try {
			String payload = "https://api.mojang.com/users/profiles/minecraft/" + name + "?at="
					+ System.currentTimeMillis();

			HttpURLConnection con = (HttpURLConnection) new URL(payload).openConnection();
			con.setRequestMethod("GET");

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) { // success
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();

				while ((inputLine = in.readLine()) != null)
					response.append(inputLine);

				in.close();
				con = null;

				return response.toString();
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	/**
	 * Format a hyphenless UUID.
	 * 
	 * @param uuid The UUID to format.
	 * @return The formated UUID.
	 */
	public static String formatUUID(String uuid) {
		StringBuilder sb = new StringBuilder(uuid);
		sb.insert(8, "-");
		sb = new StringBuilder(sb.toString());
		sb.insert(13, "-");
		sb = new StringBuilder(sb.toString());
		sb.insert(18, "-");
		sb = new StringBuilder(sb.toString());
		sb.insert(23, "-");
		return sb.toString();
	}

}
