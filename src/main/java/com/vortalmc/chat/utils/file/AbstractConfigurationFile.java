package com.vortalmc.chat.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * An abstract configuration file utility.
 * 
 * <p>
 * Extend this class in classes you wish to make configuration file
 * implementations.
 * </p>
 * 
 * @author Myles Deslippe
 */
public abstract class AbstractConfigurationFile extends File {

	/**
	 * The AbstractConfigurationFile version UID.
	 */
	private static final long serialVersionUID = -770420585934456055L;

	/**
	 * The default file configuration.
	 */
	private File defaults;

	/**
	 * The AbstractConfigurationFile constructor.
	 * 
	 * @param path The path to the configuration file.
	 */
	public AbstractConfigurationFile(final String path) {
		super(path);
	}

	/**
	 * The AbstractConfigurationFile constructor.
	 * 
	 * @param path     The path to the configuration file.
	 * @param defaults The default configuration file.
	 */
	public AbstractConfigurationFile(final String path, File defaults) {
		super(path);
		this.defaults = defaults;
	}

	/**
	 * Create the configuration file and parent directories if they do not exist on
	 * the system.
	 * 
	 * <p>
	 * This will copy the default file configuration if it is defined.
	 * </p>
	 * 
	 * @throws IOException       If an I/O error occurred.
	 *
	 * @throws SecurityException If a security manager exists and its
	 *                           <code>{@link java.lang.SecurityManager#checkWrite(java.lang.String)}</code>
	 *                           method denies write access to the file.
	 */
	public void createIfNotExists() throws IOException {
		if (!this.getParentFile().exists())
			this.getParentFile().mkdirs();

		if (!this.exists() && this.getDefaults() != null) {
			Files.copy(this.getDefaults().toPath(), this.toPath());
			this.createNewFile();
			return;
		}

		if (!this.exists())
			this.createNewFile();
	}

	/**
	 * Get the default configuration file.
	 * 
	 * @return The default configuration file.
	 */
	public File getDefaults() {
		return this.defaults;
	}

	/**
	 * Set the default configuration file.
	 * 
	 * @param defaults The default configuration file to set.
	 */
	public void setDefaults(File defaults) {
		this.defaults = defaults;
	}

	/**
	 * This method will be called when the configuration file implementation needs
	 * to be loaded.
	 */
	public abstract void load() throws IOException;

	/**
	 * This method will be called when the configuration file implementation needs
	 * to be saved.
	 */
	public abstract void save() throws IOException;
}
