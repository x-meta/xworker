package xworker.db.sql;

import java.sql.Connection;

import xworker.lang.executor.Executor;

public class SQLConnection {
	private static final String TAG = SQLConnection.class.getName();
	boolean close = false;
	Connection connection;
	
	public SQLConnection(Connection connection, boolean close) {
		this.connection = connection;
		this.close = close;
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void close() {
		if(close && connection != null) {
			try {
				connection.close();
			}catch(Exception e) {
				Executor.warn(TAG, "Close connection error", e);
			}
		}
	}
}
