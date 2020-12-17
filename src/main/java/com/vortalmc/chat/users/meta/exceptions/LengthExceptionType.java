package com.vortalmc.chat.users.meta.exceptions;

/**
 * The LengthExceptionTypes
 * 
 * <p>
 * This enum is used to define
 * {@link com.vortalmc.chat.users.meta.exceptions.LengthException
 * LengthException}s that occur when attempting to validate meta using the
 * {@link com.vortalmc.chat.users.meta.MetaValidator MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public enum LengthExceptionType {

	/**
	 * The length of the text entered was too small.
	 */
	TOO_SMALL,

	/**
	 * The length of the text entered was too big.
	 */
	TOO_BIG;

}
