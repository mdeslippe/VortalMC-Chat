package com.vortalmc.chat.utils.mysql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The SQLConnection outline.
 *
 * @author Myles Deslippe
 */
public abstract class AbstractSQLConnection {

	/**
	 * The host variable.
	 */
	private final String host;

	/**
	 * The database variable.
	 */
	private final String database;

	/**
	 * The username variable.
	 */
	private final String username;

	/**
	 * The password variable
	 */
	private final String password;

	/**
	 * The port variable.
	 */
	private final Integer port;

	/**
	 * The AbstractSQLConnection constructor.
	 *
	 * @param host     The SQL server host.
	 * @param port     The port the SQL server is listening on.
	 * @param database The database to connect to.
	 * @param username The username to login.
	 * @param password The password to login.
	 */
	public AbstractSQLConnection(String host, Integer port, String database, String username, String password) {
		this.host = host;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}

	/**
	 * Get the connection's host.
	 *
	 * @return Returns the connection's host.
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Get the connection's port.
	 *
	 * @return Returns the connection's port.
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * Get the connection's database name.
	 *
	 * @return Returns the connection's database name.
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * Get the connection's username.
	 *
	 * @return Returns the connection's username.
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Get the connection's password.
	 *
	 * @return Returns the connection's password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Start the connection pool.
	 *
	 * @throws SQLException           the SQL exception.
	 * 
	 * @throws ClassNotFoundException the class not found exception.
	 */
	public abstract void open() throws SQLException, ClassNotFoundException;

	/**
	 * Terminate the connection pool.
	 *
	 * @throws SQLException the SQL exception.
	 */
	public abstract void close() throws SQLException;

	/**
	 * Get the connection.
	 *
	 * @return the connection.
	 */
	public abstract Connection getConnection();

}