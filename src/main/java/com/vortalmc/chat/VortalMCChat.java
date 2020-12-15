package com.vortalmc.chat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import com.vortalmc.chat.commands.base.VortalMCChatCommand;
import com.vortalmc.chat.commands.message.MessageCommand;
import com.vortalmc.chat.commands.message.RespondCommand;
import com.vortalmc.chat.commands.nickname.NicknameCommand;
import com.vortalmc.chat.events.bungee.chat.PlayerChatEvent;
import com.vortalmc.chat.events.bungee.connection.PlayerJoinEvent;
import com.vortalmc.chat.events.bungee.connection.PlayerLeaveEvent;
import com.vortalmc.chat.events.custom.connection.FirstJoinEvent;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.channel.Channel;
import com.vortalmc.chat.utils.channel.ChannelManager;
import com.vortalmc.chat.utils.channel.ChannelScope;
import com.vortalmc.chat.utils.event.InternalEventManager;
import com.vortalmc.chat.utils.file.ConfigurationFile;
import com.vortalmc.chat.utils.file.FileManager;
import com.vortalmc.chat.utils.misc.cache.CacheManager;
import com.vortalmc.chat.utils.mysql.CachedRow;
import com.vortalmc.chat.utils.mysql.SQLConnection;

import litebans.api.Database;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
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
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.mis.cache.CacheManager
	 * CacheManager}.
	 */
	private CacheManager cacheManager;

	/**
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.event.InternalEventManager
	 * InternalEventManager}.
	 */
	private InternalEventManager internalEventManager;

	/**
	 * VortalMC-Chat's {@link com.vortalmc.chat.utils.channel.ChannelManager
	 * ChannelManager}.
	 */
	private ChannelManager channelManager;

	/**
	 * Called when the plugin is enabled.
	 */
	@Override
	public void onEnable() {
		
		this.fileManager = new FileManager(this);
		this.cacheManager = new CacheManager();
		this.internalEventManager = new InternalEventManager();
		this.channelManager = new ChannelManager();

		this.loadFiles();
		this.initMySQL();
		this.registerCommands();
		this.registerEvents();
		this.registerChannels();

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
		this.cacheManager = null;
		this.channelManager = null;
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
	 * Get the {@link com.vortalmc.chat.utils.event.InternalEventManager
	 * InternalEventManager} associated with managing VortalMC-Chat's custom
	 * evenets.
	 * 
	 * @return The internal event manager.
	 */
	public InternalEventManager getInternalEventManager() {
		return this.internalEventManager;
	}

	/**
	 * Get the {@link com.vortalmc.chat.utils.channel.ChannelManager ChannelManager}
	 * associated with managing VortalMC-Chat's chat channels.
	 * 
	 * @return The channel manager.
	 */
	public ChannelManager getChannelManager() {
		return this.channelManager;
	}

	/**
	 * Load all of the configuration files.
	 */
	private void loadFiles() {
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

			this.getMySQLConnection()
					.runUpdate(
							"CREATE TABLE IF NOT EXISTS `VortalMC-Chat` (\n" 
							+ "    `uuid` VARCHAR(36),\n"
							+ "    `channel` VARCHAR(255),\n" 
							+ "    `chat-color` VARCHAR(2),\n"
							+ "    `name-color` VARCHAR(2),\n" 
							+ "    `prefix` VARCHAR(255),\n"
							+ "    `suffix` VARCHAR(255),\n" 
							+ "    `nickname` VARCHAR(255),\n"
							+ "    `last-message-sender` VARCHAR(36),\n" 
							+ "    `last-message-receiver` VARCHAR(36),\n"
							+ "    `social-spy-status` BOOLEAN,\n"
							+ "    `command-spy-status` BOOLEAN,\n"
							+ "    `afk-status` BOOLEAN,\n" 
							+ "    PRIMARY KEY (`uuid`)\n" 
							+ ");");

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
		this.getProxy().getPluginManager().registerCommand(this, new NicknameCommand());
		this.getProxy().getPluginManager().registerCommand(this, new MessageCommand());
		this.getProxy().getPluginManager().registerCommand(this, new RespondCommand());
	}

	/**
	 * Register VortalMCChat's events with the
	 * {@link net.md_5.bungee.api.ProxyServer ProxyServer}.
	 */
	private void registerEvents() {
		// Bungee events.
		this.getProxy().getPluginManager().registerListener(this, new PlayerJoinEvent());
		this.getProxy().getPluginManager().registerListener(this, new PlayerLeaveEvent());
		this.getProxy().getPluginManager().registerListener(this, new PlayerChatEvent());

		// Custom events.
		this.getInternalEventManager().registerListener(new FirstJoinEvent());
	}

	/**
	 * Register all of the channels.
	 */
	private void registerChannels() {
		Configuration config = this.fileManager.getFile("config").getConfiguration().getSection("Channels");

		for (String index : config.getKeys()) {
			this.getChannelManager().registerChannel(
					new Channel(config.getString(index + ".Name"),
					config.getString(index + ".Permission"), config.getString(index + ".Format"),
					config.getStringList(index + ".Aliases").toArray(new String[0]),
					ChannelScope.valueOf(config.getString(index + ".Scope").toUpperCase())));
		}
	}

	/**
	 * Dependencies that are used by VortalMC-Chat can be accessed in this class.
	 * 
	 * @author Myles Deslippe
	 */
	public static class Dependencies {

		/**
		 * Get the LiteBans API.
		 * 
		 * @return The LiteBans API.
		 */
		public static Database getLiteBansAPI() {
			return Database.get();
		}

		/**
		 * Get the LuckPerms API.
		 * 
		 * @return The LuckPerms API.
		 */
		public static LuckPerms getLuckPermsAPI() {
			return LuckPermsProvider.get();
		}

	}

	/**
	 * Dispatch a message as a player.
	 * 
	 * <p>
	 * <strong>Note</strong>: This will only broadcast the message to the channel
	 * the player is in.
	 * </p>
	 * 
	 * @param player  The player to dispatch the message as.
	 * @param message The message to dispatch.
	 */
	public void dispatchMessage(ProxiedPlayer player, String message) {
		CachedRow row = (CachedRow) this.getCacheManager().getCache(player.getUniqueId());
		Channel channel = this.getChannelManager().getChannel(row.getValue("channel").toString());

		if (player.hasPermission(channel.getPermission())) {

			String format = channel.getFormat();

			if (!row.getValue("prefix").toString().equalsIgnoreCase("none"))
				format = format.replace("${PREFIX}", row.getValue("prefix").toString());
			else
				format = format.replace("${PREFIX}", "");

			if (!row.getValue("suffix").toString().equalsIgnoreCase("none"))
				format = format.replace("${SUFFIX}", row.getValue("suffix").toString());
			else
				format = format.replace("${SUFFIX}", "");

			if (!row.getValue("nickname").toString().equalsIgnoreCase("none"))
				format = format.replace("${NICKNAME}", row.getValue("nickname").toString());
			else
				format = format.replace("${NICKNAME}", "");

			format = format.replace("${NAME_COLOR}", row.getValue("name-color").toString());
			format = format.replace("${CHAT_COLOR}", row.getValue("chat-color").toString());
			format = format.replace("${USERNAME}", player.getName());
			format = format.replace("${NAME}", player.getName());
			format = format.replace("${DISPLAY_NAME}", User.fromProxiedPlayer(player).getsDisplayName());
			format = format.replace("${MESSAGE}", message);

			for (ProxiedPlayer index : ProxyServer.getInstance().getPlayers())
				if (index.hasPermission(channel.getPermission()))
					index.sendMessage(new TextComponent(Utils.translateColor(format)));

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
