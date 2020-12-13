package com.vortalmc.chat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.vortalmc.chat.commands.base.VortalMCChatCommand;
import com.vortalmc.chat.events.bungee.connection.PlayerJoinEvent;
import com.vortalmc.chat.events.bungee.connection.PlayerLeaveEvent;
import com.vortalmc.chat.events.custom.connection.FirstJoinEvent;
import com.vortalmc.chat.utils.event.InternalEventManager;
import com.vortalmc.chat.utils.event.defined.PlayerFirstJoinEvent;
import com.vortalmc.chat.utils.file.ConfigurationFile;
import com.vortalmc.chat.utils.file.FileManager;
import com.vortalmc.chat.utils.misc.cache.CacheManager;
import com.vortalmc.chat.utils.mysql.CachedRow;
import com.vortalmc.chat.utils.mysql.SQLConnection;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat plugin's main class.
 * 
 * @author Myles Deslippe
 */
public class VortalMCChat extends Plugin {

	/**
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.file.FileManager FileManager}.
	 */
	private FileManager fileManager;

	/**
	 * VortalMC-Chat's MySQL connection.
	 */
	private SQLConnection sqlConnection;

	/**
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.mis.cache.CacheManager CacheManager}.
	 */
	private CacheManager cacheManager;
	
	/**
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.event.InternalEventManager InternalEventManager}.
	 */
	private InternalEventManager internalEventManager;
	
	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {

		this.cacheManager = new CacheManager();
		this.internalEventManager = new InternalEventManager();
		
		this.loadFiles();
		this.initMySQL();
		this.registerCommands();
		this.registerEvents();
	}

	/**
	 * Called when the plugin is disabled.
	 */
	public void onDisable() {

		this.disconnectFromMySQL();
		this.getProxy().getPluginManager().unregisterCommands(this);
		this.getProxy().getPluginManager().unregisterListeners(this);
		this.fileManager = null;
		this.internalEventManager = null;
	}

	/**
	 * Reload the plugin.
	 */
	public void reload() {
		this.getLogger().info("Reloading the plugin");
		this.onDisable();
		this.onEnable();
		this.getLogger().info("Sucessfully reloaded the plugin");
	}

	/**
	 * Get an instance of the VortalMC-Chat plugin.
	 * 
	 * <p>
	 * Note: This will return null if the plugin is not loaded.
	 * </p>
	 * 
	 * @return The VortalMC-Chat plugin instance.
	 */
	public static VortalMCChat getInstance() {
		return (VortalMCChat) ProxyServer.getInstance().getPluginManager().getPlugin("VortalMC-Chat");
	}

	/**
	 * Get VortalMC-Chat's {@link com.vortalmc.chat.utils.file.FileManager
	 * FileManager}.
	 * 
	 * @return The file manager associated with VortalMC-Chat.
	 */
	public FileManager getFileManager() {
		return fileManager;
	}

	/**
	 * Get VortalMC-Chat's MySQL connection.
	 * 
	 * @return The MySQL connection.
	 */
	public SQLConnection getMySQLConnection() {
		return this.sqlConnection;
	}

	/**
	 * Get the {@link com.vortalmc.chat.utils.mis.cache.CacheManager CacheManager}
	 * associated with VortalMC-Chat.
	 * 
	 * @return The cache manager.
	 */
	public CacheManager getCacheManager() {
		return this.cacheManager;
	}
	
	/**
	 * Get the {@link com.vortalmc.chat.utils.event.InternalEventManager InternalEventManager} 
	 * associated with managing VortalMC-Chat's custom evenets.
	 * 
	 * @return The internal event manager.
	 */
	public InternalEventManager getInternalEventManager() {
		return this.internalEventManager;
	}
	
	/**
	 * Load all of the configuration files.
	 */
	private void loadFiles() {
		this.fileManager = new FileManager(this);

		try {
			this.getFileManager().regsiterFile("config", new ConfigurationFile(this.getDataFolder() + "/config/config.yml"));
			this.getFileManager().regsiterFile("messages", new ConfigurationFile(this.getDataFolder() + "/config/messages.yml"));
			this.getFileManager().regsiterFile("commands", new ConfigurationFile(this.getDataFolder() + "/config/commands.yml"));

			this.getFileManager().getFile("config").setDefaults(this.getResource("defaults/files/config.yml"));
			this.getFileManager().getFile("messages").setDefaults(this.getResource("defaults/files/messages.yml"));
			this.getFileManager().getFile("commands").setDefaults(this.getResource("defaults/files/commands.yml"));

			this.getFileManager().loadAllFiles();
		} catch (IOException e) {
			this.getLogger().warning("Error: Could not load all of the configuration files, disabling the plugin!");
			this.onDisable();
			return;
		}
	}

	/**
	 * Start the MySQL connection pool.
	 */
	private void initMySQL() {
		Configuration config = this.getFileManager().getFile("config").getConfiguration();
		sqlConnection = new SQLConnection(
				config.getString("MySQL.Host"),
				config.getInt("MySQL.Port"),
				config.getString("MySQL.Database"),
				config.getString("MySQL.Username"),
				config.getString("MySQL.Password")
				);

		try {
			this.getMySQLConnection().open();
			
			this.getMySQLConnection().runUpdate(
					"CREATE TABLE IF NOT EXISTS `VortalMC-Chat` (\n" + 
					"    `uuid` VARCHAR(36),\n"                      + 
					"    `chat-color` VARCHAR(2),\n"                 + 
					"    `prefix` VARCHAR(255),\n"                   + 
					"    `suffix` VARCHAR(255),\n"                   + 
					"    `nickname` VARCHAR(255),\n"                 + 
					"    `last-message-sender` VARCHAR(36),\n"       + 
					"    `last-message-receiver` VARCHAR(36),\n"     + 
					"    `afk-status` BOOLEAN,\n"                    + 
					"    PRIMARY KEY (`uuid`)\n"                     + 
					");"
					);
			
		} catch (ClassNotFoundException | SQLException e) {
			this.getLogger().warning("Could not connect to the MySQL database: " + e.getMessage());
			e.printStackTrace();
			this.sqlConnection = null;
		}
	}

	/**
	 * Close the MySQL connection pool.
	 */
	private void disconnectFromMySQL() {
		try {
			if (this.getMySQLConnection() != null)
				this.getMySQLConnection().close();
		} catch (SQLException e) {
			this.getLogger().warning("Error: An error has occurred when attempting to close the MySQL connection.");
		}
	}
	
	/**
	 * Register VortalMCChat's commands with the
	 * {@link net.md_5.bungee.api.ProxyServer ProxyServer}.
	 */
	private void registerCommands() {
		this.getProxy().getPluginManager().registerCommand(this, new VortalMCChatCommand());
	}

	/**
	 * Register VortalMCChat's events with the
	 * {@link net.md_5.bungee.api.ProxyServer ProxyServer}.
	 */
	private void registerEvents() {
		// Bungee events.
		this.getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent());
		this.getProxy().getPluginManager().registerListener(this, new PlayerLeaveEvent());
		
		// Custom events.
		this.getInternalEventManager().registerListener(new FirstJoinEvent());
	}
	
	/**
	 * Cache the players information from the database.
	 * 
	 * <p>
	 * Note: This will only return null if there is no connection to the database. 
	 * </p>
	 * 
	 * @param player The player to query.
	 * 
	 * @return The players information.
	 */
	public CachedRow getPlayerCache(ProxiedPlayer player) {
		Configuration config = this.getFileManager().getFile("config").getConfiguration();

		try {
			PreparedStatement statement = this.getMySQLConnection().getConnection().prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
			statement.setString(1, player.getUniqueId().toString());
			ResultSet results = this.getMySQLConnection().runQuery(statement);

			CachedRow row;

			if (results.next()) {
				row = new CachedRow(this.getMySQLConnection(), "VortalMC-Chat", results, 1);
				results.close();
			} else {
				
				results.close();
				
				PreparedStatement statement1 = this.getMySQLConnection().getConnection().prepareStatement(
						"INSERT INTO `VortalMC-Chat` ("  + 
							"`uuid`, "                   + 
							"`chat-color`, "             + 
							"`prefix`, "	             + 
							"`suffix`, "                 + 
							"`nickname`, "               + 
							"`last-message-sender`, "    + 
							"`last-message-receiver`, "  + 
							"`afk-status`)"              + 
						" VALUES "                       + 
							"(?, ?, ?, ?, ?, ?, ?, ?);");

				// UUID.
				statement1.setString(1, player.getUniqueId().toString());
				// Chat color.
				statement1.setString(2, config.getString("Defaults.Chat-Color"));
				// Prefix.
				statement1.setString(3, config.getString("Defaults.Prefix"));
				// Suffix
				statement1.setString(4, config.getString("Defaults.Suffix"));
				// Nickname
				statement1.setString(5, config.getString("Defults.Nickname"));
				// Last message sender.
				statement1.setNull(6, Types.NULL);
				// Last message receiver.
				statement1.setNull(7, Types.NULL);
				// Afk status.
				statement1.setBoolean(8, false);

				this.getMySQLConnection().runUpdate(statement1);
				
				PreparedStatement statement2 = this.getMySQLConnection().getConnection().prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `uuid` = ?");
				statement2.setString(1, player.getUniqueId().toString());
				ResultSet results1 = this.getMySQLConnection().runQuery(statement2);
				row = new CachedRow(this.getMySQLConnection(), "VortalMC-Chat", results1, 1);
				results1.close();

				// If the playerdata did not exist previously, that means this is the first time
				// they have joined the server, so dispatch the first join event.
				this.getInternalEventManager().dispatchEvent(new PlayerFirstJoinEvent(player));
			}

			return row;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get an internal resource.
	 * 
	 * <p>
	 * Note: This will return null if the specified resource does not exist.
	 * </p>
	 * 
	 * @param path The path to the resource.
	 * 
	 * @return The resource.
	 */
	public File getResource(String path) {

		path = "resources/" + path;

		try {
			File file = File.createTempFile(path.split("\\.")[0], path.split("\\.")[1]);
			Files.copy(this.getClass().getClassLoader().getResourceAsStream(path), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			return file;
		} catch (Exception e) {
			return null;
		}
	}
}
