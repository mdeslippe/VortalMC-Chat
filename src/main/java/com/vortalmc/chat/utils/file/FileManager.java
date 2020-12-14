package com.vortalmc.chat.utils.file;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.md_5.bungee.api.plugin.Plugin;

/**
 * The file managment utility.
 * 
 * <p>
 * This class is used to manage
 * {@link com.vortalmc.chat.utils.file.ConfigurationFile ConfigurationFiles}
 * </p>
 * 
 * @author Myles Deslippe
 */
public class FileManager {

	/**
	 * The {@link net.md_5.bungee.api.plugin.Plugin Plugin} to bind the file manager
	 * to.
	 */
	private final Plugin plugin;

	/**
	 * The configuration files registered with the file manager.
	 */
	private HashMap<String, ConfigurationFile> files = new HashMap<String, ConfigurationFile>();

	/**
	 * Create a file manager.
	 */
	public FileManager(Plugin plugin) {
		this.plugin = plugin;
	}

	/**
	 * Register a file with the file manager.
	 * 
	 * @param name The name of the file.
	 * @param file The file.
	 */
	public void regsiterFile(String name, ConfigurationFile file) {
		files.put(name, file);
	}

	/**
	 * Unregister a file with the file manager.
	 * 
	 * @param name The name of the file to unregister.
	 */
	public void unregisterFile(String name) {
		if (files.containsKey(name))
			files.remove(name);
	}

	/**
	 * Check if a file is registered with the file manager.
	 * 
	 * @param name The name of the file to check for.
	 * 
	 * @return The truth value associated with the file being registered with the
	 *         file manager.
	 */
	public boolean containsFile(String name) {
		return files.containsKey(name);
	}

	/**
	 * Check if a file is registered with the file manager.
	 * 
	 * @param file The file to check for.
	 * 
	 * @return The truth value associated with the file being registered with the
	 *         file manager.
	 */
	public boolean containsFile(ConfigurationFile file) {
		return files.containsValue(file);
	}

	/**
	 * Get a file that has been registered with the file manager.
	 * 
	 * @param name The name of the file to get.
	 * 
	 * @return The file.
	 */
	public ConfigurationFile getFile(String name) {
		return files.get(name);
	}

	/**
	 * Get all of the files that are currently registered with the file manager.
	 * 
	 * <p>
	 * Note: Making changes to this HashMap will affect the registered files.
	 * </p>
	 * 
	 * @return All of the files that are currently registered with the file manager.
	 */
	public HashMap<String, ConfigurationFile> getAllFiles() {
		return this.files;
	}

	/**
	 * Load all files that have been registered with the file manager.
	 * 
	 * @throws IOException If the files could not be loaded.
	 */
	public void loadAllFiles() throws IOException {
		Iterator<Entry<String, ConfigurationFile>> iterator = files.entrySet().iterator();

		while (iterator.hasNext()) {
			ConfigurationFile index = iterator.next().getValue();

			plugin.getLogger().info("Loading the " + index.getName() + " configuration file");
			index.createIfNotExists();
			index.load();
			plugin.getLogger().info("Successfully loaded " + index.getName());
		}
	}

	/**
	 * Save all files that have been registered with the file manager.
	 * 
	 * @throws IOException If the files could not be saved.
	 */
	public void saveAllFiles() throws IOException {
		Iterator<Entry<String, ConfigurationFile>> iterator = files.entrySet().iterator();

		while (iterator.hasNext()) {
			ConfigurationFile index = iterator.next().getValue();

			plugin.getLogger().info("Saving the " + index.getName() + " configuration file");
			index.save();
			plugin.getLogger().info("Successfully saved " + index.getName());
		}
	}

	/**
	 * Reload all file that have been registered with the file manager.
	 * 
	 * @throws IOException If the files could not be reloaded.
	 */
	public void reloadAllFiles() throws IOException {
		plugin.getLogger().info("Reloading the configuration files");
		this.loadAllFiles();
		plugin.getLogger().info("Sucessfully reloaded the configuration files");
	}

}
