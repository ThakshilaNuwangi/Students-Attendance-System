package util;

import java.sql.Connection;

public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;

    private DBConnection() {
    }

    public static DBConnection getInstance() {
        return dbConnection == null ? dbConnection = new DBConnection() : dbConnection;
    }

    public void init(Connection connection) {
        if (this.connection == null) {
            this.connection = connection;
        } else if (!this.connection.equals(connection)) {
            throw new RuntimeException("Connection is already initialized");
        }
    }

    public Connection getConnection() {
        if (this.connection == null) {
            throw new RuntimeException("Connection has not initialized");
        }
        return connection;
    }
}
