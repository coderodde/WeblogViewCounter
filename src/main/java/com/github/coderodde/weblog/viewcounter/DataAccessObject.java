package com.github.coderodde.weblog.viewcounter;

import com.github.coderodde.weblog.viewcounter.sql.SQLStatements;
import com.github.coderodde.weblog.viewcounter.exceptions.CannotAddViewException;
import com.github.coderodde.weblog.viewcounter.exceptions.CannotCreateMainTableException;
import com.github.coderodde.weblog.viewcounter.exceptions.CannotGetMostRecenetViewTimeException;
import com.github.coderodde.weblog.viewcounter.exceptions.CannotGetNumberOfViewsException;
import static com.github.coderodde.weblog.viewcounter.Utils.objs;
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
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.jdbc.pool.DataSource;

/**
 * This class implements the data access object for the view counter.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 */
public final class DataAccessObject {
    
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
     * @throws CannotCreateMainTableException if cannot create the table.
     */
    public void createTablesIfNeeded() throws CannotCreateMainTableException {
        
        try (Connection connection = getConnection()) {
            connection.createStatement()
                      .executeUpdate(SQLStatements
                                      .ViewTable
                                      .Create
                                      .CREATE_MAIN_TABLE);
            
        } catch (SQLException cause) {
            LOGGER.log(
                    Level.SEVERE, 
                    "The SQL layer failed: {0}, caused by: {1}", 
                    objs(cause.getMessage(), cause.getCause()));
            
            throw new CannotCreateMainTableException(cause);
        } catch (URISyntaxException cause) {
            LOGGER.log(
                    Level.SEVERE, 
                    "URI failed: {0}, caused by: {1}", 
                    objs(cause.getMessage(), cause.getCause()));
            
            throw new CannotCreateMainTableException(cause);
        }
    }
    
    /**
     * Adds a new view data to the database.
     * 
     * @param httpServletRequest the request object.
     * @throws com.github.coderodde.weblog.viewcounter.exceptions.CannotAddViewException
     * if adding a view data fails.
     */
    public void addView(HttpServletRequest httpServletRequest) 
            throws CannotAddViewException {
        
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
        } catch (Exception cause) {
            throw new CannotAddViewException(cause);
        }
    }
    
    /**
     * Returns the total number of views. 
     * @return the total number of views so far.
     * @throws com.github.coderodde.weblog.viewcounter.exceptions.CannotGetNumberOfViewsException
     * if cannot get the number of views.
     */
    public int getViewCount() throws CannotGetNumberOfViewsException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            try (ResultSet resultSet =
                    statement.executeQuery(
                            SQLStatements
                                    .ViewTable
                                    .Select
                                    .GET_NUMBER_OF_VIEWS)) {
                
                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Could not read the number of views.");
                }
                
                return resultSet.getInt(1);
            }
        } catch (Exception ex) {
            throw new CannotGetNumberOfViewsException(ex);
        }
    }
    
    /**
     * Returns the most recent view time stamp.
     * @return the most recent view time.
     * @throws CannotGetMostRecenetViewTimeException if reading the most recent
     * view time fails.
     */
    public ZonedDateTime getMostRecentViewTime() 
            throws CannotGetMostRecenetViewTimeException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            try (ResultSet resultSet = 
                    statement.executeQuery(
                            SQLStatements
                                    .ViewTable
                                    .Select
                                    .GET_MOST_RECENT_VIEW_TIME)) {
                
                if (!resultSet.next()) {
                    LOGGER.log(Level.SEVERE, "No most recent views.");
                    return null;
                }
                
                Timestamp mostRecentViewTimestamp = resultSet.getTimestamp(1);
                ZonedDateTime mostRecentViewZonedDateTime =
                        ZonedDateTime.ofInstant(
                                mostRecentViewTimestamp.toInstant(), 
                                ZONE_ID);
                
                return mostRecentViewZonedDateTime;
            }
            
        } catch (SQLException | URISyntaxException ex) {
            throw new CannotGetMostRecenetViewTimeException(ex);
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
                    objs(ex, ex.getCause()));
            
            throw new RuntimeException(
                    "com.mysql.cj.jdbc.Driver not found.", 
                    ex);
            
        } catch (InstantiationException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "com.mysql.cj.jdbc.Driver could not be instantiated: {0}," +
                            " caused by: {1}", 
                    objs(ex, ex.getCause()));
            
            throw new RuntimeException(
                    "com.mysql.cj.jdbc.Driver could not be instantiated.", 
                    ex);
            
        } catch (IllegalAccessException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "com.mysql.cj.jdbc.Driver could not be accessed: {0}, " + 
                            "caused by: {1}", 
                    objs(ex, ex.getCause()));
            
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
