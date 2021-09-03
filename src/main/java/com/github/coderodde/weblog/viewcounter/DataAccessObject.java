package com.github.coderodde.weblog.viewcounter;

import static com.github.coderodde.weblog.viewcounter.Util.objects;
import com.github.coderodde.weblog.viewcounter.sql.SQLStatements;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.eclipse.persistence.internal.expressions.SQLStatement;

/**
 * This class implements the data access object for the view counter.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 */
@RequestScoped
public class DataAccessObject {

    private static final Logger LOGGER = 
            Logger.getLogger(DataAccessObject.class.getName());

    private static final String EUROPE_HELSINKI_ZONE_ID = "Europe/Helsinki";
    private static final ZoneId ZONE_ID = ZoneId.of(EUROPE_HELSINKI_ZONE_ID);

    private static final String DB_URL_ENVIRONMENT_VARIABLE_NAME = 
            "CLEARDB_DATABASE_URL";

    private static final DataSource dataSource = new DataSource();
    private static final DataAccessObject INSTANCE = new DataAccessObject();

    public static DataAccessObject getInstance() {
        return INSTANCE;
    }

    /**
     * Makes sure that the main table is created.
     * 
     * @throws java.sql.SQLException if the SQL layer fails.
     * @throws java.net.URISyntaxException if the DB URI is invalid.
     */
    public void createTablesIfNeeded() throws SQLException, URISyntaxException {

        try (Connection connection = getConnection()) {
            connection.createStatement()
                      .executeUpdate(SQLStatements
                                      .ViewTable
                                      .Create
                                      .CREATE_VIEW_TABLE);

        } 
    }

    /**
     * Adds a new view data to the database.
     * 
     * @param httpServletRequest the request object.
     * 
     * @throws java.sql.SQLException if the SQL layer fails.
     * @throws java.net.URISyntaxException if the DB URI is invalid.
     */
    public void addView(HttpServletRequest httpServletRequest)
            throws SQLException, URISyntaxException {

        String host = httpServletRequest.getRemoteHost();
        int port = httpServletRequest.getRemotePort();
        String remoteAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");

        if (remoteAddress == null) {
            remoteAddress = httpServletRequest.getRemoteAddr();
        }

        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             SQLStatements.ViewTable.Insert.INSERT_VIEW)) {

            statement.setString(1, remoteAddress);
            statement.setString(2, host);
            statement.setInt(3, port);

            ZonedDateTime nowZonedDateTime = ZonedDateTime.now(ZONE_ID);

            Timestamp nowTimestamp = 
                    Timestamp.from(nowZonedDateTime.toInstant());

            statement.setTimestamp(4, nowTimestamp);
            statement.executeUpdate();
        }
    }

    /**
     * Returns the total number of views. 
     * 
     * @return the total number of views so far.
     * 
     * @throws java.sql.SQLException if the SQL layer fails.
     * @throws java.net.URISyntaxException if the DB URI is invalid.
     */
    public int getTotalViewCount() throws SQLException, URISyntaxException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet =
                    statement.executeQuery(SQLStatements
                                    .ViewTable
                                    .Select
                                    .GET_NUMBER_OF_TOTAL_VIEWS)) {

                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Could not read the number of views.");
                }

                return resultSet.getInt(1);
            }
        } 
    }
    
    public int getVisitorsViweCount(String ipAddress)
            throws SQLException, URISyntaxException {
        try (Connection connection = getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             SQLStatements
                                     .ViewTable
                                     .Select
                                     .GET_NUMBER_OF_VIEWS_OF_VISITOR)) {
            
            statement.setString(1, ipAddress);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Could not read the number of a visitor's views.");
                }
                
                return resultSet.getInt(1);
            }
        }
    }

    /**
     * Returns the most recent view time stamp.
     * @return the most recent view time.
     * 
     * @throws java.sql.SQLException if the SQL layer fails.
     * @throws java.net.URISyntaxException if the DB URI is invalid.
     */
    public ZonedDateTime getMostRecentViewTime() 
            throws SQLException, URISyntaxException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            try (ResultSet resultSet = 
                    statement.executeQuery(
                            SQLStatements
                                    .ViewTable
                                    .Select
                                    .GET_MOST_RECENT_VIEW_TIME)) {

                if (!resultSet.next()) {
                    return null;
                }

                Timestamp mostRecentViewTimestamp = resultSet.getTimestamp(1);

                if (mostRecentViewTimestamp == null) {
                    return null;
                }

                ZonedDateTime mostRecentViewZonedDateTime =
                        ZonedDateTime.ofInstant(
                                mostRecentViewTimestamp.toInstant(), 
                                ZONE_ID);

                return mostRecentViewZonedDateTime;
            }
        }
    }
    
    public ZonedDateTime getVisitorsMostRecentViewTime(String ipAddress)
            throws SQLException, URISyntaxException {
        try (Connection connection = getConnection();
             PreparedStatement statement = 
                     connection.prepareStatement(
                             SQLStatements
                                     .ViewTable
                                     .Select
                                     .GET_MOST_RECENT_VIEW_TIME_OF_VISITOR)) {
            
            statement.setString(1, ipAddress);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                
                if (!resultSet.next()) {
                    return null;
                }
                
                Timestamp mostRecentVisitorsViewTimeTimestamp = 
                        resultSet.getTimestamp(1);
                
                if (mostRecentVisitorsViewTimeTimestamp == null) {
                    return null;
                }
                
                ZonedDateTime mostRecentVisitorViewTimeZondDateTime = 
                        ZonedDateTime.ofInstant(
                                mostRecentVisitorsViewTimeTimestamp.toInstant(), 
                                ZONE_ID);
                
                return mostRecentVisitorViewTimeZondDateTime;
            }
        }
    }

    private static void loadJDBCDriverClass() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "com.mysql.cj.jdbc.Driver class not found: {0}, " + 
                            "caused by: {1}", 
                    objects(ex, ex.getCause()));

            throw new RuntimeException(
                    "com.mysql.cj.jdbc.Driver not found.", 
                    ex);

        } catch (InstantiationException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "com.mysql.cj.jdbc.Driver could not be instantiated: {0}," +
                            " caused by: {1}", 
                    objects(ex, ex.getCause()));

            throw new RuntimeException(
                    "com.mysql.cj.jdbc.Driver could not be instantiated.", 
                    ex);

        } catch (IllegalAccessException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "com.mysql.cj.jdbc.Driver could not be accessed: {0}, " + 
                            "caused by: {1}", 
                    objects(ex, ex.getCause()));

            throw new RuntimeException(
                    "com.mysql.cj.jdbc.Driver could not be accessed.", 
                    ex);
        }
    }

    static {
        loadJDBCDriverClass();
    }

    private Connection getConnection() throws SQLException, URISyntaxException {
        URI databaseURI = 
                new URI(System.getenv(DB_URL_ENVIRONMENT_VARIABLE_NAME));

        String username = databaseURI.getUserInfo().split(":")[0];
        String password = databaseURI.getUserInfo().split(":")[1];
        String databaseURL = 
                "jdbc:mysql://" + databaseURI.getHost() + databaseURI.getPath();

        return DriverManager.getConnection(databaseURL, username, password);
    }
}
