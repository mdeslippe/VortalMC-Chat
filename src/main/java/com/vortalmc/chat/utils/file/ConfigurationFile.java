package com.vortalmc.chat.utils.file;

import java.io.IOException;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * A simple YAML configuration file utility.
 * 
 * @author Myles Deslippe
 */
public class ConfigurationFile extends AbstractConfigurationFile {

	/**
	 * The ConfigurationFile version UID.
	 */
	private static final long serialVersionUID = -4896692913259217914L;

	/**
	 * The YAML configuration provider variable.
	 */
	private final ConfigurationProvider configProvider;

	/**
	 * The YAML configuration variable.
	 */
	private Configuration config;

	/**
	 * The ConfigurationFile constructor.
	 * 
	 * @param path The path to the configuration file.
	 * @throws IOException
	 */
	public ConfigurationFile(String path) throws IOException {
		super(path);
		configProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
	}

	/**
	 * Load the YAML configuration
	 * 
	 * @throws IOException If the file could not be loaded.
	 */
	@Override
	public void load() throws IOException {
		config = configProvider.load(this);
	}

	/**
	 * Save the YAML configuration.
	 * 
	 * <p>
	 * Note: This will throw an exception if {@link #load() load()} has not been
	 * called.
	 * </p>
	 * 
	 * @throws IOException If the file could not be saved.
	 */
	@Override
	public void save() throws IOException {
		configProvider.save(config, this);
	}

	/**
	 * Get the YAML configuration.
	 * 
	 * <p>
	 * Note: This will return null if {@link #load() load()} has not been called.
	 * </p>
	 * 
	 * @return The YAML configuration.
	 */
	public Configuration getConfiguration() {
		return config;
	}
}
