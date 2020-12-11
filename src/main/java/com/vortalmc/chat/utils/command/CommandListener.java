package com.vortalmc.chat.utils.command;

import java.util.ArrayList;
import java.util.Arrays;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

/**
 * A command utility.
 * 
 * <p>
 * Note: In order for the command listeners to work, the root command listener
 * must be registered with the proxy server.
 * </p>
 * 
 * @author Myles Deslippe
 */
public abstract class CommandListener extends Command {

	/**
	 * The registered sub-command listeners.
	 */
	private ArrayList<CommandListener> subCommandListeners = new ArrayList<CommandListener>();

	/**
	 * Create a CommandListener.
	 * 
	 * @param name The name of the command.
	 */
	public CommandListener(String name) {
		super(name);
	}

	/**
	 * Create a CommandListener.
	 * 
	 * @param name       The name of the command.
	 * @param permission The permission requried to execute the command.
	 * @param aliases    The command's aliases.
	 */
	public CommandListener(String name, String permission, String[] aliases) {
		super(name, permission, aliases);
	}

	/**
	 * Add a sub-command listener.
	 * 
	 * @param command The sub-command listener to add.
	 */
	public void addSubCommandListener(CommandListener command) {
		this.subCommandListeners.add(command);
	}

	/**
	 * Remove a sub-command listener.
	 * 
	 * @param command The sub-command lsitener to remove.
	 */
	public void removeSubCommandListener(CommandListener command) {
		if (this.containsSubCommandListener(command))
			this.subCommandListeners.remove(command);
	}

	/**
	 * Check if a sub-command listener is registered.
	 * 
	 * @param command The sub-command listener to check for.
	 * 
	 * @return The truth value associated with the sub-command listener being
	 *         registered.
	 */
	public boolean containsSubCommandListener(CommandListener command) {
		return subCommandListeners.contains(command);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		// Check if the CommandSender has the required permission for the command.
		if (!sender.hasPermission(this.getPermission())) {
			this.onPermissionDenied(sender, args);
			return;
		}

		// Check if there were any arguments passed with the command.
		if (args.length == 0) {
			this.onCommand(sender, args);
			return;
		}

		// Check if the first argument matches any of the sub-command listeners.
		for (CommandListener subCmd : subCommandListeners) {

			// Check if the first argument matches the sub-command's name.
			if (subCmd.getName().equalsIgnoreCase(args[0])) {
				subCmd.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
				return;
			}

			// Check if the first argument matches any of the sub-command's aliases.
			for (String index : subCmd.getAliases()) {
				if (index.equalsIgnoreCase(args[0])) {
					subCmd.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
					return;
				}
			}
		}

		// If the argument matched none of the sub-command listeners, send through to
		// the original command.
		this.onCommand(sender, args);
	}

	/**
	 * Called when the command is executed.
	 * 
	 * @param sender The executor of the command.
	 * @param args   Arguments passed in with the command.
	 */
	public abstract void onCommand(CommandSender sender, String[] args);

	/**
	 * Called when the command is executed, and the executor does not have
	 * permission to use the command.
	 * 
	 * @param sender The executor of the command.
	 * @param args   Arguments passed in with the command.
	 */
	public abstract void onPermissionDenied(CommandSender sender, String[] args);
}
