package com.vortalmc.chat.utils.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.vortalmc.chat.VortalMCChat;

/**
 * Create and manage a SQL connection.
 *
 * @author Myles Deslippe
 */
public final class SQLConnection extends AbstractSQLConnection {

	/**
	 * The connection Variable.
	 */
	private Connection connection;

	/**
	 * The SQLConnection constructor.
	 *
	 * @param host     The SQL server host.
	 * @param port     The port the SQL server is listening on.
	 * @param database The database to connect to.
	 * @param username The username to login.
	 * @param password The password to login.
	 */
	public SQLConnection(String host, Integer port, String database, String username, String password) {
		super(host, port, database, username, password);
	}

	/**
	 * Start the connection pool.
	 *
	 * @throws SQLException           the SQL exception.
	 * @throws ClassNotFoundException the class not found exception.
	 */
	@Override
	public void open() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed())
			throw new IllegalStateException();

		synchronized (VortalMCChat.getInstance()) {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + getHost() + "/" + getDatabase() + "?autoReconnect=true", getUsername(), getPassword());
			connection.setAutoCommit(false); // IMPORTANT: DO NOT REMOVE!!
		}
	}

	/**
	 * Terminate the connection pool.
	 *
	 * @throws SQLException the SQL exception.
	 */
	@Override
	public void close() throws SQLException {
		if (connection == null || connection.isClosed())
			throw new IllegalStateException();

		synchronized (VortalMCChat.getInstance()) {
			connection.close();
		}
	}

	/**
	 * Get the connection.
	 *
	 * @return Returns the SQL connection.
	 */
	@Override
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Push a new commit to the database.
	 * 
	 * @throws SQLException The SQL exception.
	 */
	public void commit() throws SQLException {
		connection.commit();
	}

	/**
	 * Run a statement.
	 * 
	 * @param statement The statement to run.
	 * 
	 * @throws SQLException The SQL exception.
	 */
	public void runStatement(String statement) throws SQLException {
		this.runStatement(connection.prepareStatement(statement));
	}

	/**
	 * Run a statement.
	 * 
	 * @param statement The statement to run.
	 * 
	 * @throws SQLException The SQL exception.
	 */
	public void runStatement(PreparedStatement statement) throws SQLException {
		statement.execute();
		this.commit();
		statement.close();
	}

	/**
	 * Run a query.
	 * 
	 * @param statement The query to execute.
	 * 
	 * @return A {@link java.sql.ResultSet ResultSet} that contains the data
	 *         generated by the query; never null.
	 * 
	 * @throws SQLException The SQL exception.
	 */
	public ResultSet runQuery(String statement) throws SQLException {
		return this.runQuery(connection.prepareStatement(statement));
	}

	/**
	 * Run a query.
	 * 
	 * @param statement The query to execute.
	 * 
	 * @return A {@link java.sql.ResultSet ResultSet} that contains the data
	 *         generated by the query; never null.
	 * 
	 * @throws SQLException The SQL exception.
	 */
	public ResultSet runQuery(PreparedStatement statement) throws SQLException {
		ResultSet results = statement.executeQuery();
		return results;
	}

	/**
	 * Run an update.
	 * 
	 * @param statement The statement to execute.
	 * 
	 * @throws SQLException The SQL Exception.
	 */
	public void runUpdate(String statement) throws SQLException {
		this.runUpdate(connection.prepareStatement(statement));
	}

	/**
	 * Run an update.
	 * 
	 * @param statement The statement to execute.
	 * 
	 * @throws SQLException The SQL Exception.
	 */
	public void runUpdate(PreparedStatement statement) throws SQLException {
		statement.executeUpdate();
		this.commit();
		statement.close();
	}

}