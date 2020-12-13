package com.vortalmc.chat.utils.event;

/**
 * The InvalidEventHandlerException
 * 
 * <p>
 * This exception will be thrown when an event handler is registered without
 * specifying the type of event it intended to handle.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class InvalidEventHandlerException extends RuntimeException {

	/**
	 * The InvalidEventHandlerException serial version UID.
	 */
	private static final long serialVersionUID = 5159848849853387893L;

	/**
	 * The Invalid Event Handler Exception constructor.
	 * 
	 * @param reason The reason for throwing the exception.
	 */
	public InvalidEventHandlerException(final String reason) {
		super(reason);
	}

}
