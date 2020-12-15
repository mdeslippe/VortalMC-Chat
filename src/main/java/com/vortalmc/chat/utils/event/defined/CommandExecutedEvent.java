package com.vortalmc.chat.utils.event.defined;

import com.vortalmc.chat.utils.event.Event;

import net.md_5.bungee.api.connection.Connection;

/**
 * The comand executed event.
 * 
 * @author Myles Deslippe
 */
public class CommandExecutedEvent implements Event {

	/**
	 * The connection that executed the command.
	 */
	private final Connection executor;

	/**
	 * The command that was executeed.
	 */
	private final String command;

	/**
	 * Create a new command executed event.
	 * 
	 * @param connection The connection that executed the command.
	 * @param message    The command that was executed.
	 */
	public CommandExecutedEvent(Connection executor, String command) {
		this.executor = executor;
		this.command = command;
	}

	/**
	 * Get the connection that executed the command.
	 * 
	 * @return The connection that executed the command.
	 */
	public Connection getExecutor() {
		return this.executor;
	}

	/**
	 * Get the command that was executed.
	 * 
	 * @return The command that was executed.
	 */
	public String getCommand() {
		return this.command;
	}

}
