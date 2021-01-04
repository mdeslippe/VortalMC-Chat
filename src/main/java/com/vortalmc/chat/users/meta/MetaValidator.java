package com.vortalmc.chat.users.meta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenColorException;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
import com.vortalmc.chat.users.meta.exceptions.InvalidColorException;
import com.vortalmc.chat.users.meta.exceptions.LengthException;
import com.vortalmc.chat.users.meta.exceptions.LengthExceptionType;
import com.vortalmc.chat.users.meta.exceptions.NicknameCannotBePlayerNameException;
import com.vortalmc.chat.users.meta.exceptions.NicknameInUseException;
import com.vortalmc.chat.utils.Utils;
import com.vortalmc.chat.utils.misc.cache.Cache;
import com.vortalmc.chat.utils.mysql.CachedRow;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.config.Configuration;

/**
 * The VortalMC-Chat MetaValidator.
 * 
 * <p>
 * This class is a utility that can be used to validate
 * {@link com.vortalmc.chat.users.User User} meta before applying it.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class MetaValidator {

	/**
	 * The configuration file associated with VortalMC-Chat that contains the meta
	 * validation settings.
	 */
	private final Configuration config;

	/**
	 * Create a new MetaValidator.
	 * 
	 * @param plugin The VortalMC-Chat plugin instance to bind the MetaValidator to.
	 */
	public MetaValidator(VortalMCChat plugin) {
		this.config = plugin.getFileManager().getFile("config").getConfiguration();
	}

	/**
	 * Validate a prefix.
	 * 
	 * @param prefix The prefix to validate.
	 * 
	 * @throws ForbiddenTextException If forbidden text is found in the prefix.
	 * @throws LengthException        If the prefix is too short or too long.
	 */
	public void validatePrefix(String prefix) throws ForbiddenTextException, LengthException {
		
		// Check if the prefix contains forbidden text.
		for (String index : config.getStringList("Prefix.Forbidden-Text"))
			if (prefix.contains(index) || Utils.stripColorCodes(prefix).contains(index))
				throw new ForbiddenTextException(index);
		
		// Check if the prefix meets the minimum prefix length requirement.
		if (prefix.length() < config.getInt("Prefix.Minimum-Length"))
			throw new LengthException(prefix.length(), LengthExceptionType.TOO_SMALL);

		// Check if the prefix exceeds the maximum prefix length requirement.
		if (prefix.length() > config.getInt("Prefix.Maximum-Length"))
			throw new LengthException(prefix.length(), LengthExceptionType.TOO_BIG);
	}

	/**
	 * Validate a suffix.
	 * 
	 * @param suffix The suffix to validate.
	 * 
	 * @throws ForbiddenTextException If forbidden text is found in the suffix.
	 * @throws LengthException        If the suffix is too short or too long.
	 */
	public void validateSuffix(String suffix) throws ForbiddenTextException, LengthException {
		
		// Check if the suffix contains forbidden text.
		for (String index : config.getStringList("Suffix.Forbidden-Text"))
			if (suffix.contains(index) || Utils.stripColorCodes(suffix).contains(index))
				throw new ForbiddenTextException(index);

		// Check if the suffix meets the minimum suffix length requirement.
		if (suffix.length() < config.getInt("Prefix.Minimum-Length"))
			throw new LengthException(suffix.length(), LengthExceptionType.TOO_SMALL);

		// Check if the suffix exceeds the maximum suffix length requirement.
		if (suffix.length() > config.getInt("Prefix.Maximum-Length"))
			throw new LengthException(suffix.length(), LengthExceptionType.TOO_BIG);
	}

	/**
	 * Validate a nickname.
	 * 
	 * @param nickname The nickname to validate.
	 * 
	 * @throws ForbiddenTextException              If forbidden text is found in the
	 *                                             nickname.
	 * 
	 * @throws NicknameInUseException              If the nickname is already being
	 *                                             using by anoter player.
	 * 
	 * @throws LengthException                     If the nickname is too short or
	 *                                             too long.
	 * 
	 * @throws NicknameCannotBePlayerNameException If the nickname is another
	 *                                             player's username.
	 */
	public void validateNickname(String nickname) throws ForbiddenTextException, NicknameInUseException,
			LengthException, NicknameCannotBePlayerNameException {

		// Check for forbidden characters.
		for (String index : config.getStringList("Nickname.Forbidden-Text"))
			if (nickname.contains(index) || Utils.stripColorCodes(nickname).contains(index))
				throw new ForbiddenTextException(index);
		
		// Check if the user is trying set their nickname to none.
		if(Utils.stripColorCodes(nickname).equalsIgnoreCase("none"))
			throw new ForbiddenTextException("none");

		// Check if any cached user has the nickname.
		for(Cache index : VortalMCChat.getInstance().getCacheManager().getCache().values()) {
			String str = ((CachedRow) index).getValue("nickname").toString();

			if (str.equalsIgnoreCase(nickname) || str.equalsIgnoreCase(Utils.stripColorCodes(nickname)))
				throw new NicknameInUseException(nickname);
		}

		// Check if any user in the database has the nickname.
		try {
			PreparedStatement statement = VortalMCChat.getInstance().getMySQLConnection().getConnection()
					.prepareStatement("SELECT * FROM `VortalMC-Chat` WHERE `nickname` = ? OR `nickname` = ?;");
			statement.setString(1, nickname);
			statement.setString(2, Utils.stripColorCodes(nickname));
			ResultSet results = VortalMCChat.getInstance().getMySQLConnection().runQuery(statement);
			if (results.next())
				throw new NicknameInUseException(nickname);
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		// Check if the nickname meets the minimum nickname length requirement.
		if (Utils.stripColorCodes(nickname).length() < config.getInt("Nickname.Minimum-Length"))
			throw new LengthException(Utils.stripColorCodes(nickname).length(), LengthExceptionType.TOO_SMALL);
		
		// Check if the nickname exceeds the maximum nickname length requirement.
		if (Utils.stripColorCodes(nickname).length() > config.getInt("Nickname.Maximum-Length"))
			throw new LengthException(Utils.stripColorCodes(nickname).length(), LengthExceptionType.TOO_BIG);

		// Check if the nickname is the username of a player that has joined the server before.
		String playerDataResponse = Utils.getMojangPlayerData(Utils.stripColorCodes(nickname));
		
		if (playerDataResponse != null) {

			User user = User.fromUUID(UUID.fromString(Utils.formatUUID(new Gson().fromJson(playerDataResponse, JsonObject.class).get("id").getAsString())));

			if (ProxyServer.getInstance().getPlayer(user.getUUID()) != null || user.isInDatabase())
				throw new NicknameCannotBePlayerNameException(nickname);
		}
	}

	/**
	 * Validate a chat color.
	 * 
	 * @param color The color to validate.
	 * 
	 * @throws InvalidColorException If the color specified is not a valid color.
	 * @throws ForbiddenColorException If an forbidden color has been detected.
	 */
	public void validateChatColor(String color) throws InvalidColorException, ForbiddenColorException {
		
		// Check if the color specified is a valid color code.
		if(!color.matches("[&][a-f|A-F|0-9|K-R|k-r]"))
			throw new InvalidColorException(color);
		
		// Check if the color specified is allowed.
		for (String index : config.getStringList("Chat-Color.Allowed-Colors"))
			if (color.contains(index))
				return;
		
		throw new ForbiddenColorException(color);
	}

	/**
	 * Validate a name color..
	 * 
	 * @param color The color to validate.
	 * 
	 * @throws InvalidColorException If the color specified is not a valid color.
	 * @throws ForbiddenColorException If an forbidden color has been detected.
	 */
	public void validateNameColor(String color) throws InvalidColorException, ForbiddenColorException {
		
		// Check if the color specified is a valid color code.
		if(!color.matches("[&][a-f|A-F|0-9|K-R|k-r]"))
			throw new InvalidColorException(color);
		
		// Check if the color specified is allowed.
		for (String index : config.getStringList("Name-Color.Allowed-Colors"))
			if (color.contains(index))
				return;
		
		throw new ForbiddenColorException(color);
	}

}
