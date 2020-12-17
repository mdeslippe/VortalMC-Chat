package com.vortalmc.chat.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
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
	public static TextComponent translateColor(final String message) {
		return new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
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
	 * Query playerdata from the Mojang servers.
	 * 
	 * <p>
	 * <strong>Note</strong> This will return null if the player does not exist or
	 * the servers are offline.
	 * </p>
	 * 
	 * @param uuid The uuid of the player to query.
	 * 
	 * @return The result of the query.
	 */
	public static String getMojangPlayerData(UUID uuid) {

		try {
			String payload = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString();

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

	/**
	 * Strip the color codes off of a message.
	 * 
	 * @param message The message to strip.
	 * 
	 * @return The stripped message.
	 */
	public static String stripColorCodes(String message) {
		return message.replaceAll("[&][a-f|A-F|0-9|K-R|k-r]", "");
	}

}
