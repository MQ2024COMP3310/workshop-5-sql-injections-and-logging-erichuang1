package workshop05code;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Import for logging exercise
import java.io.FileInputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SQLiteConnectionManager {
    // Start code logging exercise
    static {
        // must set before the Logger
        // loads logging.properties from the classpath
        try {// resources\logging.properties
            LogManager.getLogManager().readConfiguration(new FileInputStream("resources/logging.properties"));
        } catch (SecurityException | IOException e1) {
            e1.printStackTrace();
        }
    }

    private static final Logger logger = Logger.getLogger(SQLiteConnectionManager.class.getName());
    // End code logging exercise

    private String databaseURL = "";

    private static final String WORDLE_DROP_TABLE_STRING = "DROP TABLE IF EXISTS wordlist;";
    private static final String WORDLE_CREATE_STRING = "CREATE TABLE wordlist (\n"
            + " id integer PRIMARY KEY,\n"
            + " word text NOT NULL\n"
            + ");";

    private static final String VALID_WORDS_DROP_TABLE_STRING = "DROP TABLE IF EXISTS validWords;";
    private static final String VALID_WORDS_CREATE_STRING = "CREATE TABLE validWords (\n"
            + " id integer PRIMARY KEY,\n"
            + " word text NOT NULL\n"
            + ");";

    /**
     * Set the database file name in the sqlite project to use
     *
     * @param fileName the database file name
     */
    public SQLiteConnectionManager(String filename) {
        databaseURL = "jdbc:sqlite:sqlite/" + filename;
        logger.log(Level.INFO, "databaseURL=" + databaseURL);
    }

    /**
     * Connect to a sample database
     *
     * @param fileName the database file name
     */
    public void createNewDatabase(String fileName) {

        try (Connection conn = DriverManager.getConnection(databaseURL)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                logger.log(Level.INFO, "Driver name: " + meta.getDriverName());
                logger.log(Level.INFO, "A new database has been created.");

            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
            System.out.println(e.getMessage());
        }
    }

    /**
     * Check that the file has been cr3eated
     *
     * @return true if the file exists in the correct location, false otherwise. If
     *         no url defined, also false.
     */
    public boolean checkIfConnectionDefined() {
        if (databaseURL.equals("")) {
            logger.log(Level.WARNING, "Connection undefined. Empty databaseURL.");
            return false;
        } else {
            try (Connection conn = DriverManager.getConnection(databaseURL)) {
                if (conn != null) {
                    logger.log(Level.INFO, "Database connected. databaseURL=" + databaseURL);
                    return true;
                } else {
                    logger.log(Level.WARNING, "Connection undefined.");
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
                System.out.println(e.getMessage());
                return false;
            }
        }
        return false;
    }

    /**
     * Create the table structures (2 tables, wordle words and valid words)
     *
     * @return true if the table structures have been created.
     */
    public boolean createWordleTables() {
        if (databaseURL.equals("")) {
            logger.log(Level.WARNING, "Connection undefined. Empty databaseURL.");
            return false;
        } else {
            try (Connection conn = DriverManager.getConnection(databaseURL);
                    Statement stmt = conn.createStatement()) {
                logger.log(Level.INFO, "Database connected. databaseURL=" + databaseURL);
                stmt.execute(WORDLE_DROP_TABLE_STRING);
                logger.log(Level.INFO, "Execute WORDLE_DROP_TABLE_STRING.");
                stmt.execute(WORDLE_CREATE_STRING);
                logger.log(Level.INFO, "Execute WORDLE_CREATE_STRING.");
                stmt.execute(VALID_WORDS_DROP_TABLE_STRING);
                logger.log(Level.INFO, "Execute VALID_WORDS_DROP_TABLE_STRING.");
                stmt.execute(VALID_WORDS_CREATE_STRING);
                logger.log(Level.INFO, "Execute VALID_WORDS_CREATE_STRING.");
                return true;

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
                System.out.println(e.getMessage());
                return false;
            }
        }
    }

    /**
     * Take an id and a word and store the pair in the valid words
     * 
     * @param id   the unique id for the word
     * @param word the word to store
     */
    public void addValidWord(int id, String word) {

        String sql = "INSERT INTO validWords(id,word) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(databaseURL);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            logger.log(Level.INFO, "Database connected. databaseURL=" + databaseURL);
            pstmt.setInt(1, id);
            pstmt.setString(2, word);
            pstmt.executeUpdate();
            logger.log(Level.INFO, "Execute insertion.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
            System.out.println(e.getMessage());
        }

    }

    /**
     * Possible weakness here?
     * 
     * @param guess the string to check if it is a valid word.
     * @return true if guess exists in the database, false otherwise
     */
    public boolean isValidWord(String guess) {
        String sql = "SELECT count(id) as total FROM validWords WHERE word like ?;";

        try (Connection conn = DriverManager.getConnection(databaseURL);
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            logger.log(Level.INFO, "Database connected. databaseURL=" + databaseURL);
            stmt.setString(1, guess);
            ResultSet resultRows = stmt.executeQuery();
            logger.log(Level.INFO, "Execute word check.");
            if (resultRows.next()) {
                int result = resultRows.getInt("total");
                return (result >= 1);
            }

            return false;

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Unspecified error: " + e.toString());
            System.out.println(e.getMessage());
            return false;
        }

    }
}
