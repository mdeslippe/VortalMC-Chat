package com.vortalmc.chat.users.meta.exceptions;

/**
 * The ForbiddenColorException.
 * 
 * <p>
 * This exception will be thrown if a forbidden color is detected when
 * validating meta with the {@link com.vortalmc.chat.users.meta.MetaValidator
 * MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class ForbiddenColorException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = -4871361830987984655L;

	/**
	 * The forbidden color that was detected.
	 */
	private final String color;

	/**
	 * Create a new ForbiddenColorException.
	 * 
	 * @param color The forbidden color that was detected.
	 */
	public ForbiddenColorException(String color) {
		super(color);
		this.color = color;
	}

	/**
	 * Get the forbidden color that was detected.
	 * 
	 * @return The forbidden color that was detected.
	 */
	public String getColor() {
		return this.color;
	}

}
