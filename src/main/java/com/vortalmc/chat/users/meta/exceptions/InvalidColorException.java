package com.vortalmc.chat.users.meta.exceptions;

/**
 * The InvalidColorException.
 * 
 * <p>
 * This exception will be thrown if an invalid color is detected when validating
 * meta with the {@link com.vortalmc.chat.users.meta.MetaValidator
 * MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class InvalidColorException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = -7497914087110368452L;

	/**
	 * The invalid color that was detected.
	 */
	private final String color;

	/**
	 * Create a new InvalidColorException.
	 * 
	 * @param color The invalid color that was detected.
	 */
	public InvalidColorException(String color) {
		super(color);
		this.color = color;
	}

	/**
	 * Get the invalid color that was detected.
	 * 
	 * @return The invalid color that was detected.
	 */
	public String getForbiddenColor() {
		return this.color;
	}

}
