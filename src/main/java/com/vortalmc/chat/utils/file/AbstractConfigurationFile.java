package com.vortalmc.chat.utils.file;

import java.io.File;
import java.io.IOException;

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
	 * The AbstractConfigurationFile constructor.
	 * 
	 * @param path The path to the configuration file.
	 */
	public AbstractConfigurationFile(final String path) {
		super(path);
	}

	/**
	 * Create the configuration file and parent directories if they do not exist on
	 * the system.
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

		if (!this.exists())
			this.createNewFile();
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
