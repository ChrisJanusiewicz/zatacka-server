package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Be sure to use sqlite JDBC driver

public class DBManager {

    private static DBManager instance;
    private final String databaseURL;
    private DebugLevel debugLevel;
    private Connection conn;


    public DBManager(String databaseURL) {

        this.databaseURL = databaseURL;

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            init(databaseURL, DebugLevel.ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Connection getConn() {
        return conn;
    }

    private void init(String url, DebugLevel debugLevel) throws ClassNotFoundException, SQLException {
        this.debugLevel = debugLevel;

        // Load jdbc driver
        log("Attempting to load database driver library...", DebugLevel.BASIC);
        Class.forName("org.sqlite.JDBC");  // SQLITE
        // Class.forName("oracle.jdbc.driver.OracleDriver");  // ORACLE

        log("Attempting to connect to database...", DebugLevel.BASIC);
        conn = DriverManager.getConnection(url);

        log("Connected.", DebugLevel.ALL);

        initTables();
    }

    private void initTables() throws SQLException {
        createTableUser();
    }

    private void createTableUser() throws SQLException {

        String stmtStr = "CREATE TABLE IF NOT EXISTS user(\n" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "username VARCHAR(16) UNIQUE NOT NULL,\n" +
                "password_hash BLOB NOT NULL,\n" +
                "salt BLOB NOT NULL,\n" +
                "last_login DATETIME NOT NULL,\n" +
                "registered_at DATETIME NOT NULL);";
        Statement stmt = conn.createStatement();
        stmt.execute(stmtStr);
    }

    private void log(String message, DebugLevel messageLevel) {
        if (debugLevel.isAtLeast(messageLevel)) {
            System.out.println("[DB]: " + message);
        }
    }

    private void log(String message, DebugLevel messageLevel, Object... args) {
        if (debugLevel.isAtLeast(messageLevel)) {
            System.out.println(String.format("[DB]: " + message, args));
        }
    }


    /**
     * Enum used for logging of diagnostic and error messages within DBManager
     * Each type of diagnostic and error message has a DebugLevel which is compared
     * with the DebugLevel to which the class is set.
     */
    public enum DebugLevel {

        NONE(0),
        ERROR(1),
        BASIC(2),
        ALL(3);

        private Integer importance;

        DebugLevel(Integer importance) {
            this.importance = importance;
        }

        public boolean isAtLeast(DebugLevel other) {
            return this.importance >= other.importance;
        }

    }

}
