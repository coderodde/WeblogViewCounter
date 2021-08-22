package com.github.coderodde.weblog.viewcounter;

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
import javassist.CannotCompileException;
import javax.servlet.http.HttpServletRequest;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

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
    
    private static final String DB_URL_ENVIRONMENT_VARIABLE_NAME = 
            "CLEARDB_DATABASE_URL";
    
    private static final DataSource dataSource = new DataSource();
    private static final DataAccessObject INSTANCE = new DataAccessObject();
    
    public static DataAccessObject getInstance() {
        return INSTANCE;
    }
    
    private Connection getConnection() throws SQLException, URISyntaxException {
        URI databaseURI = 
                new URI(System.getenv(DB_URL_ENVIRONMENT_VARIABLE_NAME));
        
        String username = databaseURI.getUserInfo().split(":")[0];
        String password = databaseURI.getUserInfo().split(":")[1];
        String databaseURL = "jdbc:mysql://" + databaseURI.getHost() + databaseURI.getPath();
        
        return DriverManager.getConnection(databaseURL, username, password);
        
//        try {
//            return dataSource.getConnection();
//        } catch (SQLException ex) {
//            LOGGER.log(
//                    Level.SEVERE, 
//                    "Could not create a database connection: {0}, " +
//                            "caused by: {1}", 
//                    objs(ex.getMessage(), ex.getCause()));
//            
//            throw ex;
//        }
    }
    
    /**
     * Makes sure that the main table is created.
     * 
     * @throws CannotCreateMainTableException if cannot create the table.
     */
    public void createTablesIfNeeded() throws CannotCreateMainTableException {
        createMainTableIfNeeded();
    }
    
    /**
     * Adds a new view data to the database.
     * 
     * @param httpServletRequest the request object.
     * @throws SQLException if the SQL layer fails.
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
                             SQLStatements.MainTable.Insert.INSERT_VIEW)) {
            
            statement.setString(1, remoteAddress);
            statement.setString(2, host);
            statement.setInt(3, port);
            
            ZonedDateTime nowZonedDateTime = 
                    ZonedDateTime.now(ZoneId.of(EUROPE_HELSINKI_ZONE_ID));
            
            Timestamp nowTimestamp = 
                    Timestamp.from(nowZonedDateTime.toInstant());
            
            statement.setTimestamp(4, nowTimestamp);
            statement.executeUpdate();
        }
    }
    
    /**
     * Returns the total number of views. 
     */
    public JSONResponseObject getViewCount() {
        JSONResponseObject jsonResponseObject = new JSONResponseObject();
        
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            try (ResultSet resultSet =
                    statement.executeQuery(
                            SQLStatements
                                    .MainTable
                                    .Select
                                    .GET_NUMBER_OF_VIEWS)) {
                
                if (!resultSet.next()) {
                    throw new IllegalStateException(
                            "Could not read the number of views.");
                }
                
                int numberOfViews = resultSet.getInt(1);
                
                jsonResponseObject.succeeeded = true;
                jsonResponseObject.numberOfViews = numberOfViews;
            }
        } catch (SQLException ex) {
            jsonResponseObject.succeeeded = false;
        } catch (URISyntaxException ex) {
            jsonResponseObject.succeeeded = false;
        }
        
        return jsonResponseObject;
    }
    
//    private static void initializeDataSource() {
//        
//        PoolProperties p = new PoolProperties();
//        p.setCommitOnReturn(true);
//        p.setDefaultAutoCommit(true);
//        p.setDriverClassName("com.mysql.cj.jdbc.Driver");
//        p.setFairQueue(true);
//        p.setMaxActive(5);
//        p.setMaxAge(10000_000L);
//        p.setMaxIdle(5);
//        p.setMaxWait(600000_000);
//        p.setMinEvictableIdleTimeMillis(60000_000);
//        p.setMinIdle(0);
//        p.setRemoveAbandoned(true);
//        p.setRemoveAbandonedTimeout(6000);
//        p.setLogAbandoned(true);
//        p.setSuspectTimeout(3000000);
//        p.setTestOnBorrow(false);
//        p.setTestOnConnect(false);
//        p.setTestOnReturn(false);
//        p.setTestWhileIdle(true);
//        
//        String url = "jdbc:" + System.getenv(DB_URL_ENVIRONMENT_VARIABLE_NAME);
//        
//        p.setUrl(url);
//        p.setUseDisposableConnectionFacade(false);
//        p.setUseLock(false);
//        p.setUseStatementFacade(false);
//        p.setValidationQuery("SELECT 1");
//        p.setValidationQueryTimeout(-1); // This is the default value.
//        
//        dataSource.setPoolProperties(p);
//    } 
    
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
//        initializeDataSource();
        loadJDBCDriverClass();
    }
    
    private void createMainTableIfNeeded()
            throws CannotCreateMainTableException {
        
        try (Connection connection = getConnection()) {
            connection.createStatement()
                      .executeUpdate(SQLStatements
                                      .MainTable
                                      .Create
                                      .CREATE_MAIN_TABLE);
            
        } catch (SQLException cause) {
            LOGGER.log(
                    Level.SEVERE, 
                    "The SQL layer failed: {0}, caused by: {1}", 
                    objs(cause.getMessage(), cause.getCause()));
            
            CannotCreateMainTableException ex = 
                    new CannotCreateMainTableException(
                            "Cannot create the main table 'view'.", 
                            cause);
            
            throw ex;
        } catch (URISyntaxException cause) {
            LOGGER.log(
                    Level.SEVERE, 
                    "URI failed: {0}, caused by: {1}", 
                    objs(cause.getMessage(), cause.getCause()));
            
            CannotCreateMainTableException ex = 
                    new CannotCreateMainTableException(
                            "Cannot create the main table '" + 
                                    SQLDefinitions.ViewTable.NAME + 
                                    "'.", cause);
            
            throw ex;
        }
    }
}
