package com.vortalmc.chat.users.meta.exceptions;

/**
 * The LengthException
 * 
 * <p>
 * This exception will be thrown if a text does not meet the length
 * requiredments defined in the config file when validating meta with the
 * {@link com.vortalmc.chat.users.meta.MetaValidator MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class LengthException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = 2123994252217441832L;

	/**
	 * The length of the text.
	 */
	private final int length;

	/**
	 * The type of length exception.
	 */
	private final LengthExceptionType type;

	/**
	 * Create a new LengthException.
	 * 
	 * @param length The length of the text.
	 * @param type   The type of the exception.
	 */
	public LengthException(int length, LengthExceptionType type) {
		super(type.toString() + ": " + length);

		this.length = length;
		this.type = type;
	}

	/**
	 * Get the length of the text.
	 * 
	 * @return The length of the text.
	 */
	public int getLength() {
		return this.length;
	}

	/**
	 * Get the type of length exception.
	 * 
	 * @return The type of length exception.
	 */
	public LengthExceptionType getType() {
		return this.type;
	}

}
