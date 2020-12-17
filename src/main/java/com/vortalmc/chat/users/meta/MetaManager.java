package com.vortalmc.chat.users.meta;

import com.vortalmc.chat.VortalMCChat;
import com.vortalmc.chat.VortalMCChat.Dependencies;
import com.vortalmc.chat.users.User;

import net.luckperms.api.cacheddata.CachedDataManager;

/**
 * Manage {@link com.vortalmc.chat.users.User User}'s meta.
 * 
 * @author Myles Deslippe
 */
public class MetaManager {

	/**
	 * The {@link com.vortalmc.chat.users.User User} the MetaManager is bound to.
	 */
	private User user;
	
	/**
	 * Create a new MetaManager.
	 * 
	 * @param user The {@link com.vortalmc.chat.users.User User} who's meta will be managed.
	 */
	public MetaManager(User user) {
		this.user = user;
	}

	/**
	 * Get the User's current chat color.
	 * 
	 * @return The User's current chat color.
	 */
	public String getChatColor() {
		return user.getUserData().getValue("chat-color").toString();
	}

	/**
	 * Set the User's chat color.
	 * 
	 * @param color The User's new chat color.
	 */
	public void setChatColor(String color) {
		user.getUserData().setColumn("chat-color", color);
	}
	
	/**
	 * Set the User's chat color to the default chat color.
	 */
	public void resetChatColor() {
		user.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Chat-Color"));
	}
	
	/**
	 * Get the User's name color.
	 * 
	 * @return The User's name color.
	 */
	public String getNameColor() {
		return user.getUserData().getValue("name-color").toString();
	}

	/**
	 * Set the User's name color.
	 * 
	 * @param color The User's new name color.
	 */
	public void setNameColor(String color) {
		user.getUserData().setColumn("name-color", color);
	}

	/**
	 * Set the User's name color to the default name color.
	 */
	public void resetNameColor() {
		user.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Name-Color"));
	}
	
	/**
	 * Get the User's prefix.
	 * 
	 * @return The User's prefix.
	 */
	public String getPrefix() {
		return user.getUserData().getValue("prefix").toString();
	}

	/**
	 * Set the User's prefix.
	 * 
	 * @param prefix The User's new prefix.
	 */
	public void setPrefix(String prefix) {
		user.getUserData().setColumn("prefix", prefix);
	}

	/**
	 * Set the User's prefix to the default prefix.
	 */
	public void resetPrefix() {
		user.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Prefix"));
	}
	
	/**
	 * Check if the User has a prefix.
	 * 
	 * @return The truth value associated with the User having a prefix.
	 */
	public boolean hasPrefix() {
		return !this.getPrefix().equalsIgnoreCase("none");
	}
	
	/**
	 * Remove the User's prefix.
	 */
	public void removePrefix() {
		this.setPrefix("none");
	}
	
	/**
	 * Get the User's suffix.
	 * 
	 * @return The User's suffix.
	 */
	public String getSuffix() {
		return user.getUserData().getValue("suffix").toString();
	}

	/**
	 * Set the User's suffix.
	 * 
	 * @param suffix The User's new suffix.
	 */
	public void setSuffix(String suffix) {
		user.getUserData().setColumn("suffix", suffix);
	}
	
	/**
	 * Set the User's suffix to the default suffix.
	 */
	public void resetSuffix() {
		user.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Suffix"));
	}
	
	/**
	 * Check if the User has a suffix.
	 * 
	 * @return The truth value associated with the User having a suffix.
	 */
	public boolean hasSuffix() {
		return !this.getSuffix().equalsIgnoreCase("none");
	}
	
	/**
	 * Remove the User's suffix.
	 */
	public void removeSuffix() {
		this.setSuffix("none");
	}
	
	/**
	 * Get the User's nickname.
	 * 
	 * @return The User's nickname.
	 */
	public String getNickname() {
		return user.getUserData().getValue("nickname").toString();
	}

	/**
	 * Set the User's nickname.
	 * 
	 * @param nickname The User's new nickname.
	 */
	public void setNickname(String nickname) {
		user.getUserData().setColumn("nickname", nickname);
	}

	/**
	 * Set the User's nickname to the default nickname.
	 */
	public void resetNickname() {
		user.setChatChannel(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Nickname"));
	}
	
	/**
	 * Remove the User's nickname.
	 */
	public void removeNickname() {
		this.setNickname("none");
	}
	
	/**
	 * Check if the User has a nickname.
	 * 
	 * @return The truth value associated with the User having a nickname.
	 */
	public boolean hasNickname() {
		return !this.getNickname().equalsIgnoreCase("none");
	}
	
	/**
	 * This will return the preferred name for the User.
	 * 
	 * <p>
	 * If the User has a nickname, the nickname will be returned. <br>
	 * If the User does not have a nickname, their username will be returned.
	 * 
	 * @return The User's preferred name.
	 */
	public String getPreferedName() {
		
		String color = "";
		if(!this.getNameColor().equalsIgnoreCase(VortalMCChat.getInstance().getFileManager().getFile("config").getConfiguration().getString("Defaults.Name-Color")))
			color += this.getNameColor();
		
		if(this.hasNickname())
			return color + this.getNickname();
		else
			return color + user.getAsProxiedPlayer().getName();
	}
	
	/**
	 * Get the User's displayname.
	 * 
	 * @return The User's displayname.
	 */
	public String getsDisplayName() {
		CachedDataManager data = Dependencies.getLuckPermsAPI().getUserManager().getUser(user.getUUID()).getCachedData();

		String nickname = String.valueOf(this.getNickname());
		String nameColor = String.valueOf(this.getNameColor());
		String prefix = data.getMetaData().getPrefix();
		String suffix = data.getMetaData().getSuffix();
		String username = user.getAsProxiedPlayer().getName();

		String buffer = "";

		if (prefix != null)
			buffer = buffer + "&r" + prefix + " ";

		if (!nickname.equalsIgnoreCase("none"))
			buffer = buffer + nameColor + nickname + " ";
		else
			buffer = buffer + nameColor + username + " ";

		if (suffix != null)
			buffer = buffer + "&r" + suffix + " ";

		return buffer.substring(0, buffer.length() - 1);
	}
	
}
