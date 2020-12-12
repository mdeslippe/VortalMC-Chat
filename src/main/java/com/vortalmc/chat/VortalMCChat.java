package com.vortalmc.chat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import com.vortalmc.chat.commands.base.VortalMCChatCommand;
import com.vortalmc.chat.utils.file.ConfigurationFile;
import com.vortalmc.chat.utils.file.FileManager;
import com.vortalmc.chat.utils.misc.cache.CacheManager;
import com.vortalmc.chat.utils.mysql.SQLConnection;

import net.md_5.bungee.api.ProxyServer;
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
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {

		this.loadFiles();
		this.initMySQL();
		this.registerCommands();
		this.registerEvents();
		this.cacheManager = new CacheManager();
		
	}

	/**
	 * Called when the plugin is disabled.
	 */
	public void onDisable() {

		this.disconnectFromMySQL();
		this.getProxy().getPluginManager().unregisterCommands(this);
		this.getProxy().getPluginManager().unregisterListeners(this);
		this.fileManager = null;
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
	protected SQLConnection getMySQLConnection() {
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
