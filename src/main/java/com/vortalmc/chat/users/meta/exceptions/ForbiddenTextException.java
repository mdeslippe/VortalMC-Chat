package com.vortalmc.chat.users.meta.exceptions;

/**
 * The ForbiddenTextException.
 * 
 * <p>
 * This exception will be thrown if a forbidden text is detected when validating
 * meta with the {@link com.vortalmc.chat.users.meta.MetaValidator
 * MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class ForbiddenTextException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = 6669794841402477483L;

	/**
	 * The forbidden text that was detected.
	 */
	private final String text;

	/**
	 * Create a new ForbiddenTextException.
	 * 
	 * @param text The forbidden text that was detected.
	 */
	public ForbiddenTextException(String text) {
		super(text);
		this.text = text;
	}

	/**
	 * Get the forbidden text that was detected.
	 * 
	 * @return The forbidden text that was detected.
	 */
	public String getForbiddenText() {
		return this.text;
	}

}
