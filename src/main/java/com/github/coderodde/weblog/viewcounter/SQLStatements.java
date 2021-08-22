package com.github.coderodde.weblog.viewcounter;

/**
 * This class defines all the SQL statements in the application.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 * @since 1.6 (Aug 22, 2021)
 */
public final class SQLStatements {
    
    /**
     * The statements for the main table.
     */
    public static final class MainTable {
        
        /**
         * The create table statements.
         */
        public static final class Create {
            
            /**
             * Creates a table for storing the views unless there is one already
             * in the database.
             */
            public static final String CREATET_MAIN_TABLE = 
                    "CREATE TABLE IF NOT EXISSTS `" + 
                    SQLDefinitions.ViewTable.NAME + 
                    "` (" +
                    SQLDefinitions.ViewTable.Id.NAME + " " +
                    SQLDefinitions.ViewTable.Id.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.IPAddress.NAME + " " + 
                    SQLDefinitions.ViewTable.IPAddress.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.HostName.NAME + " " +
                    SQLDefinitions.ViewTable.HostName.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.PortNumber.NAME + " " +
                    SQLDefinitions.ViewTable.PortNumber.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.ViewTimestamp.NAME + " " + 
                    SQLDefinitions.ViewTable.ViewTimestamp.TYPE + ",\n" +
                    "PRIMARY KEY (" + SQLDefinitions.ViewTable.Id.NAME + "));";
                    
        }
        
        /**
         * The insert data statements. 
         */
        public static final class Insert {
            
            /**
             * Inserts a new view into the database.
             */
            public static final String INSERT_VIEW = 
                    "INSERT INTO `" + SQLDefinitions.ViewTable.NAME + "` (\"" +
                    SQLDefinitions.ViewTable.IPAddress.NAME + "\", " +
                    SQLDefinitions.ViewTable.HostName.NAME + "\", " +
                    SQLDefinitions.ViewTable.PortNumber.NAME + "\", " +
                    SQLDefinitions.ViewTable.ViewTimestamp.NAME + "\") " +
                    "VALUES (?, ?, ?, ?);";
        }
        
        /**
         * The select data statements.
         */
        public static final class Select {
            
            /**
             * Returns the total number of views. 
             */
            public static final String GET_NUMBER_OF_VIEWS = 
                    "SELECT COUNT(*) FROM ´" + SQLDefinitions.ViewTable.NAME +
                    "`;";
        }
    }
}
