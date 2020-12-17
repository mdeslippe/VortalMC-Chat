package com.vortalmc.chat.users.meta.exceptions;

/**
 * The NicknameCannotBePlayerNameException
 * 
 * <p>
 * This exception will be thrown if the nickname is a player's username that has
 * played on the server before when validating nicknames with the
 * {@link com.vortalmc.chat.users.meta.MetaValidator MetaValidator}.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class NicknameCannotBePlayerNameException extends Exception {

	/**
	 * The exception's serial ID.
	 */
	private static final long serialVersionUID = 2886438012899114604L;

	/**
	 * The nickname that is a playername.
	 */
	private final String nickname;

	/**
	 * Create a new NicknameCannotBePlayerNameException.
	 * 
	 * @param nickname The nickname that is a player's name.
	 */
	public NicknameCannotBePlayerNameException(String nickname) {
		super(nickname);

		this.nickname = nickname;
	}

	/**
	 * Get the nickname that is a player's name.
	 * 
	 * @return The nickname that is a player's name.
	 */
	public String getNickname() {
		return this.nickname;
	}

}
