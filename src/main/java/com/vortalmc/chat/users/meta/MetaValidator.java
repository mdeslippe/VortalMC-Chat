package com.vortalmc.chat.users.meta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.UUID;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.users.User;
import com.vortalmc.chat.users.meta.exceptions.ForbiddenTextException;
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

		for (String index : config.getStringList("Prefix.Forbidden-Text"))
			if (prefix.contains(index) || Utils.stripColorCodes(prefix).contains(index))
				throw new ForbiddenTextException(index);

		if (prefix.length() < config.getInt("Prefix.Minimum-Length"))
			throw new LengthException(prefix.length(), LengthExceptionType.TOO_SMALL);

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
		for (String index : config.getStringList("Suffix.Forbidden-Text"))
			if (suffix.contains(index) || Utils.stripColorCodes(suffix).contains(index))
				throw new ForbiddenTextException(index);

		if (suffix.length() < config.getInt("Prefix.Minimum-Length"))
			throw new LengthException(suffix.length(), LengthExceptionType.TOO_SMALL);

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

		Iterator<Entry<Object, Cache>> iterator = VortalMCChat.getInstance().getCacheManager().getCache().entrySet().iterator();

		// Check if any cached user has the nickname.
		while (iterator.hasNext()) {
			String str = ((CachedRow) iterator.next().getValue()).getValue("nickname").toString();

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
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (Utils.stripColorCodes(nickname).length() < config.getInt("Nickname.Minimum-Length"))
			throw new LengthException(Utils.stripColorCodes(nickname).length(), LengthExceptionType.TOO_SMALL);

		if (Utils.stripColorCodes(nickname).length() > config.getInt("Nickname.Maximum-Length"))
			throw new LengthException(Utils.stripColorCodes(nickname).length(), LengthExceptionType.TOO_BIG);

		String playerDataResponse = Utils.getMojangPlayerData(Utils.stripColorCodes(nickname));
		if (playerDataResponse != null) {

			User user = User.fromUUID(UUID.fromString(Utils
					.formatUUID(new Gson().fromJson(playerDataResponse, JsonObject.class).get("id").getAsString())));

			if (ProxyServer.getInstance().getPlayer(user.getUUID()) != null || user.isInDatabase())
				throw new NicknameCannotBePlayerNameException(nickname);
		}
	}

	/**
	 * Validate chat colors.
	 * 
	 * @param color The color to validate.
	 * 
	 * @throws ForbiddenTextException If an forbidden color has been detected.
	 */
	public void validateChatColor(String color) throws ForbiddenTextException {
		for (String index : config.getStringList("Chat-Color.Forbidden-Colors"))
			if (color.contains(index))
				throw new ForbiddenTextException(index);
	}

	/**
	 * Validate name colors.
	 * 
	 * @param color The color to validate.
	 * 
	 * @throws ForbiddenTextException If an forbidden color has been detected.
	 */
	public void validateNameColor(String color) throws ForbiddenTextException {
		for (String index : config.getStringList("Name-Color.Forbidden-Colors"))
			if (color.contains(index))
				throw new ForbiddenTextException(index);
	}

}
