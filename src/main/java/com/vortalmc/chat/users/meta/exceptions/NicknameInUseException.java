package com.vortalmc.chat.users.meta.exceptions;

/**
 * The NicknameInUseException
 * 
 * <p>
 * This exception will be thrown if the nickname is already in use when
 * validating nicknames with the
 * {@link com.vortalmc.chat.users.meta.MetaValidator MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class NicknameInUseException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = -8604684362275129054L;

	/**
	 * The nickname that is already in use.
	 */
	private final String nickname;

	/**
	 * Create a new NicknameInUseException.
	 * 
	 * @param nickname The nickname that is already in use.
	 */
	public NicknameInUseException(String nickname) {
		super(nickname);
		this.nickname = nickname;
	}

	/**
	 * Get the nickname that is already in use.
	 * 
	 * @return The nickname that is already in use.
	 */
	public String getNickname() {
		return this.nickname;
	}

}
