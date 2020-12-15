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
import com.vortalmc.chat.VortalMCChat.Dependencies;
import com.vortalmc.chat.utils.mysql.CachedRow;

import net.luckperms.api.cacheddata.CachedDataManager;
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
	 * Create a new User.
	 * 
	 * @param player The {@linkplain net.md_5.bungee.api.connection.ProxiedPlayer
	 *               ProxiedPlayer} instance of the user.
	 */
	protected User(ProxiedPlayer player) {

		this.player = player;
		this.uuid = player.getUniqueId();

	}

	/**
	 * Create a new User.
	 * 
	 * @param uuid The UUID of the user.
	 */
	protected User(UUID uuid) {
		this.player = VortalMCChat.getInstance().getProxy().getPlayer(uuid);
		this.uuid = uuid;
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
	 * Get the User's current chat color.
	 * 
	 * @return The User's current chat color.
	 */
	public String getChatColor() {
		return this.getUserData().getValue("chat-color").toString();
	}

	/**
	 * Set the User's chat color.
	 * 
	 * @param color The User's new chat color.
	 */
	public void setChatColor(String color) {
		this.getUserData().setColumn("chat-color", color);
	}

	/**
	 * Get the User's name color.
	 * 
	 * @return The User's name color.
	 */
	public String getNameColor() {
		return this.getUserData().getValue("name-color").toString();
	}

	/**
	 * Set the User's name color.
	 * 
	 * @param color The User's new name color.
	 */
	public void setNameColor(String color) {
		this.getUserData().setColumn("name-color", color);
	}

	/**
	 * Get the User's prefix.
	 * 
	 * @return The User's prefix.
	 */
	public String getPrefix() {
		return this.getUserData().getValue("prefix").toString();
	}

	/**
	 * Set the User's prefix.
	 * 
	 * @param prefix The User's new prefix.
	 */
	public void setPrefix(String prefix) {
		this.getUserData().setColumn("prefix", prefix);
	}

	/**
	 * Get the User's suffix.
	 * 
	 * @return The User's suffix.
	 */
	public String getSuffix() {
		return this.getUserData().getValue("suffix").toString();
	}

	/**
	 * Set the User's suffix.
	 * 
	 * @param suffix The User's new suffix.
	 */
	public void setSuffix(String suffix) {
		this.getUserData().setColumn("suffix", suffix);
	}

	/**
	 * Get the User's nickname.
	 * 
	 * @return The User's nickname.
	 */
	public String getNickname() {
		return this.getUserData().getValue("nickname").toString();
	}

	/**
	 * Set the User's nickname.
	 * 
	 * @param nickname The User's new nickname.
	 */
	public void setNickname(String nickname) {
		this.getUserData().setColumn("nickname", nickname);
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

		VortalMCChat.getInstance().getCacheManager().addCache(uuid, this.getUserDataFromDatabase());
		return this.getUserDataFromCache();
	}

	/**
	 * Strictly get the User's data from <strong>cache</strong>
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
	 * Strictly get the User's data from the <strong>database</strong>
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
			PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection().prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
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
			PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection().prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
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

			if (VortalMCChat.getInstance().getCacheManager().containsCache(this.getUUID())) {
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
	 * Get the User's displayname.
	 * 
	 * @return The User's displayname.
	 */
	public String getsDisplayName() {
		CachedRow row = (CachedRow) VortalMCChat.getInstance().getCacheManager().getCache(this.getUUID());
		CachedDataManager data = Dependencies.getLuckPermsAPI().getUserManager().getUser(this.getUUID()).getCachedData();

		String nickname = String.valueOf(row.getValue("nickname"));
		String nameColor = String.valueOf(row.getValue("name-color"));
		String prefix = data.getMetaData().getPrefix();
		String suffix = data.getMetaData().getSuffix();
		String username = player.getName();

		String buffer = "";

		if (prefix != null)
			buffer = buffer + "&r" + prefix + " ";

		if (!nickname.equalsIgnoreCase("none"))
			buffer = buffer + nameColor + nickname + " ";
		else
			buffer = buffer + nameColor + username + " ";

		if (suffix != null)
			buffer = buffer + "&r" + suffix + " ";

		return buffer.substring(0, buffer.length() - 1);
	}

}
