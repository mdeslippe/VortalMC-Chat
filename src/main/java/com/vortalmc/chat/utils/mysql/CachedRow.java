package com.vortalmc.chat.utils.mysql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.vortalmc.chat.utils.misc.cache.Cache;

/**
 * A rowset caching utility.
 * 
 * </p>
 * In order for {@link #update()} to work correctly, there must be a uuid column
 * in the databse that uniquely identifies the row that is being updated.
 * </p>
 * 
 * @author Myles Deslippe
 */
public class CachedRow implements Cache {

	/**
	 * The rowset's columns and values.
	 */
	private HashMap<String, Object> map;

	/**
	 * The SQL connection that will be used when updating the row.
	 */
	private final SQLConnection connection;

	/**
	 * The table the update will effect.
	 */
	private final String table;

	/**
	 * Create a cached row.
	 * 
	 * @param connection The connection to execute updates to the row.
	 * @param table      The table the row is in.
	 * @param results    The resultset containing the row.
	 * @param row        The index of the row.
	 * @throws SQLException If an exception occurs.
	 */
	public CachedRow(SQLConnection connection, String table, ResultSet results, int row) throws SQLException {

		this.connection = connection;
		this.table = table;
		this.map = new HashMap<String, Object>();

		while (results.getRow() != row)
			results.next();

		ResultSetMetaData meta = results.getMetaData();

		for (int i = 1; i < meta.getColumnCount(); i++)
			map.put(meta.getColumnName(i), results.getObject(i));
	}

	/**
	 * Get a {@link java.util.HashMap HashMap} containing the columns and values
	 * from the database.
	 * 
	 * <p>
	 * To apply changes made in the hashmap to the database call {@link #update()
	 * update()}.
	 * </p>
	 * 
	 * @return
	 */
	public HashMap<String, Object> getAllColumns() {
		return this.map;
	}

	/**
	 * Get the value at a column in the row.
	 * 
	 * <p>
	 * <strong>Note</strong>: This will return <strong>null</strong> if the specified <strong>column does not exist</strong>.
	 * </p>
	 * 
	 * @param column The target column.
	 * 
	 * @return The value at the column.
	 */
	public Object getValue(String column) {
		return this.map.get(column);
	}

	/**
	 * Update the value at a column.
	 * 
	 * @param column The column to update.
	 * @param value  The new value.
	 */
	public void updateColumn(String column, Object value) {
		this.map.put(column, value);
	}

	/**
	 * Update the value at a column.
	 * 
	 * @param column The column to update.
	 * @param value  The new value.
	 */
	public void setColumn(String column, Object value) {
		this.map.put(column, value);
	}
	
	/**
	 * Update the database with the local cache.
	 * 
	 * </p>
	 * In order for {@link #update()} to work correctly, there must be a uuid column
	 * in the databse that uniquely identifies the row that is being updated.
	 * </p>
	 * 
	 * @throws SQLException If an exception occurs.
	 */
	public void push() throws SQLException {

		// Get the keys.
		String keys = "";

		Iterator<Entry<String, Object>> iterator = map.entrySet().iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, Object> index = iterator.next();
			keys = keys + "`" + index.getKey() + "` = ?, ";
		}

		// Remove the extra ", "
		keys = keys.substring(0, keys.length() - 2);

		// Prepare the statement.
		String sql = "UPDATE `{1}` SET {2} WHERE {3};";
		sql = sql.replace("{1}", table);
		sql = sql.replace("{2}", keys);
		sql = sql.replace("{3}", "`uuid` = '" + map.get("uuid") + "'");
		PreparedStatement statement = connection.getConnection().prepareStatement(sql);

		// Insert the values.
		iterator = map.entrySet().iterator();

		for (int i = 1; iterator.hasNext(); i++) {
			Map.Entry<String, Object> index = iterator.next();
			statement.setObject(i, index.getValue());
		}
		
		// Run the update.
		connection.runUpdate(statement);
	}
}
