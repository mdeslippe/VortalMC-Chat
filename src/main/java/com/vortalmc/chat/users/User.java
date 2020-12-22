package com.vortalmc.chat.users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.meta.MetaManager;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.mysql.CachedRow;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

/**
 * Represents a {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
 * ProxiedPlayer} who is currently connected to VortalMC.
 * 
 * @author Myles Deslippe
 */
public class User {

	/**
	 * The {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer ProxiedPlayer}
	 * the User is encapsulating.
	 */
	private final ProxiedPlayer player;

	/**
	 * The UUID of the {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 * ProxiedPlayer} the User is encapsulating.
	 */
	private UUID uuid;

	/**
	 * The User's MetaManager.
	 */
	private final MetaManager metaManager;

	/**
	 * Create a new User.
	 * 
	 * @param player The {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 *               ProxiedPlayer} instance of the user.
	 */
	protected User(ProxiedPlayer player) {

		this.player = player;
		this.uuid = player.getUniqueId();
		this.metaManager = new MetaManager(this);

	}

	/**
	 * Create a new User.
	 * 
	 * @param uuid The UUID of the user.
	 */
	protected User(UUID uuid) {
		this.player = VortalMCChat.getInstance().getProxy().getPlayer(uuid);
		this.uuid = uuid;
		this.metaManager = new MetaManager(this);
	}

	/**
	 * Get a User from a {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 * ProxiedPlayer} instance.
	 * 
	 * @param player The {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 *               ProxiedPlayer} instance.
	 * 
	 * @return The user.
	 */
	public static User fromProxiedPlayer(ProxiedPlayer player) {
		return new User(player);
	}

	/**
	 * Get a User from a {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 * ProxiedPlayer}'s UUID.
	 * 
	 * @param uuid The {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 *             ProxiedPlayer}'s UUID.
	 * 
	 * @return The user.
	 */
	public static User fromUUID(UUID uuid) {
		return new User(uuid);
	}

	/**
	 * Get the User as a {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 * ProxiedPlayer}.
	 * 
	 * @return The user as a
	 *         {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 *         ProxiedPlayer}.
	 */
	public ProxiedPlayer getAsProxiedPlayer() {
		return this.player;
	}

	/**
	 * Get the User's UUID.
	 * 
	 * @return the users UUID.
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	public MetaManager getMeta() {
		return this.metaManager;
	}

	/**
	 * Get the User's current chat channel.
	 * 
	 * @return The User's current chat channel.
	 */
	public String getChatChannel() {
		return this.getUserData().getValue("channel").toString();
	}

	/**
	 * Set the User's chat channel.
	 * 
	 * @param channel The User's new chat channel.
	 */
	public void setChatChannel(String channel) {
		this.getUserData().setColumn("channel", channel);
	}

	/**
	 * Set the User's chat channel to the default channel.
	 */
	public void resetChatChannel() {
		this.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Channel"));
	}

	/**
	 * Get the UUID of the player who lasted messaged the User.
	 * 
	 * @return The UUID of the player who last messaged the User.
	 */
	public String getLastMessageSender() {
		return this.getUserData().getValue("last-message-sender").toString();
	}

	/**
	 * Set the UUID of the player who last messaged the User.
	 * 
	 * @param sender The UUID of the player who last messaged the User.
	 */
	public void setLastMessageSender(String sender) {
		this.getUserData().setColumn("last-message-sender", sender);
	}

	/**
	 * Set the UUID of the player who last messaged the User.
	 * 
	 * @param sender The UUID of the player who last messaged the User.
	 */
	public void setLastMessageSender(UUID sender) {
		this.getUserData().setColumn("last-message-sender", sender.toString());
	}

	/**
	 * Check if the User has been messaged before.
	 * 
	 * @return The truth value associated with the User being messaged before.
	 */
	public boolean hasLastMessageSender() {
		return !this.getLastMessageSender().equalsIgnoreCase("none");
	}

	/**
	 * Get the player who the User last messaged's UUID.
	 * 
	 * @return The player who the User last messaged's UUID.
	 */
	public String getLastMessageReceiver() {
		return this.getUserData().getValue("last-message-receiver").toString();
	}

	/**
	 * Set the player who the User last messaged.
	 * 
	 * @param receiver The UUID of the player who the User last messaged.
	 */
	public void setLastMessageReceiver(String receiver) {
		this.getUserData().setColumn("last-message-receiver", receiver);
	}

	/**
	 * Set the player who the User last messaged.
	 * 
	 * @param receiver The UUID of the player who the User last messaged.
	 */
	public void setLastMessageReceiver(UUID receiver) {
		this.getUserData().setColumn("last-message-receiver", receiver.toString());
	}

	/**
	 * Check if the User has been messaged anyone before..
	 * 
	 * @return The truth value associated with the User messaged someone before.
	 */
	public boolean hasLastMessageReceiver() {
		return !this.getLastMessageReceiver().equalsIgnoreCase("none");
	}

	/**
	 * Check if the User has social spy enabled.
	 * 
	 * @return The truth value associated with the User having social spy enabled.
	 */
	public boolean hasSocialSpyEnabled() {
		return new Boolean(this.getUserData().getValue("social-spy-status").toString());
	}

	/**
	 * Enabled social spy for the User.
	 */
	public void enableSocialSpy() {
		this.getUserData().setColumn("social-spy-status", true);
	}

	/**
	 * Disable social spy for the User.
	 */
	public void disableSocialSpy() {
		this.getUserData().setColumn("social-spy-status", false);
	}

	/**
	 * Check if the User has command spy enabled.
	 * 
	 * @return The truth value associated with the User having command spy enabled.
	 */
	public boolean hasCommandSpyEnabled() {
		return new Boolean(this.getUserData().getValue("command-spy-status").toString());
	}

	/**
	 * Enabled command spy for the User.
	 */
	public void enableCommandSpy() {
		this.getUserData().setColumn("command-spy-status", true);
	}

	/**
	 * Disable command spy for the User.
	 */
	public void disableCommandSpy() {
		this.getUserData().setColumn("command-spy-status", false);
	}

	/**
	 * Check if the User is AFK.
	 * 
	 * @return The truth value associated with the User being AFK.
	 */
	public boolean isAFK() {
		return new Boolean(this.getUserData().getValue("afk-status").toString());
	}

	/**
	 * Mark the User as AFK.
	 */
	public void enabledAFK() {
		this.getUserData().setColumn("afk-status", true);
	}

	/**
	 * Mark the User as not AFK.
	 */
	public void disbleAFK() {
		this.getUserData().setColumn("afk-status", false);
	}

	/**
	 * Check if the User's data is currently beign cached.
	 * 
	 * @return The truth value associated with the User's data currently being
	 *         cached.
	 */
	public boolean dataIsCached() {
		return VortalMCChat.getInstance().getCacheManager().containsCache(this.getUUID());
	}

	/**
	 * Get the User's data.
	 * 
	 * <p>
	 * This will first check the cache for the User's data, if it is not found
	 * there, it will query it from the database and cache it before returning it.
	 * </p>
	 * 
	 * @return The User's data.
	 */
	public CachedRow getUserData() {
		if (this.dataIsCached())
			return this.getUserDataFromCache();

		if (!this.isInDatabase()) {
			try {
				this.addUserToDatabase();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		VortalMCChat.getInstance().getCacheManager().addCache(uuid, this.getUserDataFromDatabase());
		return this.getUserDataFromCache();
	}

	/**
	 * Strictly get the User's data from <strong>cache</strong>.
	 * 
	 * <p>
	 * Note: If the User's data is not currently cached, this will return null. <br>
	 * Use {@link #dataIsCached()} to check if the User's data is currently cached.
	 * </p>
	 * 
	 * @return The User's data from cache.
	 */
	public CachedRow getUserDataFromCache() {
		return (CachedRow) VortalMCChat.getInstance().getCacheManager().getCache(this.getUUID());
	}

	/**
	 * Strictly get the User's data from the <strong>database</strong>.
	 * 
	 * <p>
	 * This data will not always match the cached data. The cached data is always up
	 * to date.
	 * </p>
	 * 
	 * <p>
	 * Note: This will only return null if there is no connection to the database,
	 * or the User does not exist in the database. <br>
	 * Use {@link #isInDatabase()} to check if the user is in the database.
	 * </p>
	 * 
	 * @return The players information.
	 */
	public CachedRow getUserDataFromDatabase() {

		try {
			PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection()
					.prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
			statement.setString(1, this.getUUID().toString());
			ResultSet results = VortalMCChat.getInstance().getMySQLConnection().runQuery(statement);

			CachedRow row = null;

			if (results.next()) {
				row = new CachedRow(VortalMCChat.getInstance().getMySQLConnection(), "VortalMC-Chat", results, 1);
				results.close();
			}

			return row;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Insert the User into the database.
	 * 
	 * <p>
	 * It is advised that {@link #isInDatabase()} is used <strong>before</strong>
	 * inserting the User into the database to prevent duplicate entries.
	 * </p>
	 * 
	 * @throws SQLException If an SQL exception occurrs.
	 */
	public void addUserToDatabase() throws SQLException {
		Configuration config = VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration();

		PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection().prepareStatement(
				"INSERT INTO `VortalMC-Chat` (" 
				+ "`uuid`, " 
				+ "`channel`, " 
				+ "`chat-color`, "
				+ "`name-color`, " 
				+ "`prefix`, " 
				+ "`suffix`, " 
				+ "`nickname`, " 
				+ "`last-message-sender`, "
				+ "`last-message-receiver`, " 
				+ "`social-spy-status`, " 
				+ "`command-spy-status`, "
				+ "`afk-status`)" 
				+ " VALUES " 
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
				);

		// UUID.
		statement.setString(1, this.getUUID().toString());
		// Channel.
		statement.setString(2, config.getString("Defaults.Channel"));
		// Chat color.
		statement.setString(3, config.getString("Defaults.Chat-Color"));
		// Name color.
		statement.setString(4, config.getString("Defaults.Name-Color"));
		// Prefix.
		if (config.getString("Defaults.Prefix") != "")
			statement.setString(5, config.getString("Defaults.Prefix"));
		else
			statement.setString(5, "none");
		// Suffix
		if (config.getString("Defaults.Suffix") != "" && config.getString("Defaults.Suffix") != null)
			statement.setString(6, config.getString("Defaults.Suffix"));
		else
			statement.setString(6, "none");
		// Nickname.
		if (config.getString("Defults.Nickname") != "" && config.getString("Defaults.Suffix") != null)
			statement.setString(7, config.getString("Defaults.Nickname"));
		else
			statement.setString(7, "none");
		// Last message sender.
		statement.setString(8, "none");
		// Last message receiver.
		statement.setString(9, "none");
		// Social spy status.
		statement.setBoolean(10, false);
		// Command spy status.
		statement.setBoolean(11, false);
		// Afk status.
		statement.setBoolean(12, false);

		VortalMCChat.getInstance().getMySQLConnection().runUpdate(statement);
	}

	/**
	 * Check if the User is in the database.
	 * 
	 * @return The truth value assocated with the User being in the database.
	 */
	public boolean isInDatabase() {
		try {
			PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection()
					.prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
			statement.setString(1, this.getUUID().toString());
			ResultSet results = VortalMCChat.getInstance().getMySQLConnection().runQuery(statement);
			return results.next();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Update the User's row in the `VortalMC-Chat` table.
	 * 
	 * <p>
	 * Note: If the User's data is currently being cached, it will affect the cache
	 * only until the cache is pushed to the database, otherwise it will go directly
	 * to the database.
	 * </p>
	 * 
	 * @param column The column you want to update.
	 * @param value  The value you want to insert into the column
	 * 
	 * @return If the update was successfull or not
	 * 
	 * @throws SQLException If an SQL exception occurs.
	 */
	public boolean updateColumn(String column, String value) throws SQLException {

		if (this.isInDatabase()) {

			if (this.dataIsCached()) {
				CachedRow row = this.getUserDataFromCache();
				row.setColumn(column, value);
			} else {
				CachedRow row = this.getUserDataFromDatabase();
				row.setColumn(column, value);
				row.push();
			}
			return true;
		}
		return false;
	}

	/**
	 * Query playerdata from the Mojang servers.
	 * 
	 * <p>
	 * <strong>Note</strong> This will return null if the player does not exist or
	 * the servers are offline.
	 * </p>
	 * 
	 * @return The result of the query.
	 */
	public String getMojangUserData() {

		try {
			String payload = "https://sessionserver.mojang.com/session/minecraft/profile/" + this.getUUID().toString();

			HttpURLConnection con = (HttpURLConnection) new URL(payload).openConnection();
			con.setRequestMethod("GET");

			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
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
	 * Get the User who's username or nickname best matches the partial name.
	 * 
	 * <p>
	 * <strong>Note</strong> This will return null if there are no users online, or
	 * no players matched the partial name.
	 * </p>
	 * 
	 * @param name The partial name to match.
	 * 
	 * @return The best possible match to the partial name.
	 */
	public static User fromPartialName(String name) {

		if (ProxyServer.getInstance().getPlayer(name) != null)
			return User.fromProxiedPlayer(ProxyServer.getInstance().getPlayer(name));

		ProxiedPlayer bestMatchingPlayer = null;
		int amountOfMatchingDigits = 0;

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			
			User user = User.fromProxiedPlayer(player);
			
			if(user.getMeta().hasNickname() && Utils.stripColorCodes(name).equalsIgnoreCase(Utils.stripColorCodes(user.getMeta().getNickname())))
				return user;
			
			int nameMatchCount = getAmountOfMatchingDigitsInARow(player.getName(), ChatColor.stripColor(name));
			int nickMatchCount = getAmountOfMatchingDigitsInARow(
					ChatColor.stripColor(user.getMeta().getNickname()),
					ChatColor.stripColor(name)
					);

			if (nameMatchCount > amountOfMatchingDigits) {
				amountOfMatchingDigits = nameMatchCount;
				bestMatchingPlayer = player;
			}

			if (nickMatchCount > amountOfMatchingDigits && !User.fromProxiedPlayer(player).getMeta().getNickname().equalsIgnoreCase("none")) {
				
				amountOfMatchingDigits = nickMatchCount;
				bestMatchingPlayer = player;
			}
		}

		if (bestMatchingPlayer == null)
			return null;
		else
			return User.fromProxiedPlayer(bestMatchingPlayer);
	}

	/**
	 * Get the User who's =nickname best matches the partial name.
	 * 
	 * <p>
	 * <strong>Note</strong> This will return null if there are no users online, or
	 * no players matched the partial name.
	 * </p>
	 * 
	 * @param name The partial name to match.
	 * 
	 * @return The best possible match to the partial name.
	 */
	public static User fromPartialNickname(String nickname) {
		
		ProxiedPlayer bestMatchingPlayer = null;
		int amountOfMatchingDigits = 0;

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			int nickMatchCount = getAmountOfMatchingDigitsInARow(
					ChatColor.stripColor(User.fromProxiedPlayer(player).getMeta().getNickname()),
					ChatColor.stripColor(nickname));

			if (nickMatchCount > amountOfMatchingDigits && !User.fromProxiedPlayer(player).getMeta().getNickname().equalsIgnoreCase("none")) {
				amountOfMatchingDigits = nickMatchCount;
				bestMatchingPlayer = player;
			}

		}

		if (bestMatchingPlayer == null)
			return null;
		else
			return User.fromProxiedPlayer(bestMatchingPlayer);
	}

	/**
	 * Get the highest amount of consecutive digits in a string, that match another
	 * string.
	 * 
	 * @param str    The string to search in.
	 * @param target The string to search for.
	 * 
	 * @return The highest amount consecutive digits in the target string that are
	 *         consecutive in the str string.
	 */
	private static int getAmountOfMatchingDigitsInARow(String str, String target) {

		char[] str1 = str.toLowerCase().toCharArray(), str2 = target.toLowerCase().toCharArray();

		int topMatchCount = 0, buffer = 0;

		for (int i = 0; i <= str.length() - 1; i++) {

			if (buffer >= str2.length)
				return buffer;

			if (str1[i] == str2[buffer]) {
				buffer++;
			} else if (buffer > topMatchCount) {
				topMatchCount = buffer;
				buffer = 0;
			} else {
				buffer = 0;
			}
		}

		return topMatchCount;
	}

}
