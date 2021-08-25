package com.github.coderodde.weblog.viewcounter.sql;

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
    public static final class ViewTable {

        /**
         * The create table statements.
         */
        public static final class Create {

            /**
             * Creates a table for storing the views unless there is one already
             * in the database.
             */
            public static final String CREATE_MAIN_TABLE = 
                    "CREATE TABLE IF NOT EXISTS " + 
                    SQLDefinitions.ViewTable.NAME + 
                    " (\n" +
                    SQLDefinitions.ViewTable.Id.NAME + " " +
                    SQLDefinitions.ViewTable.Id.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.IPAddress.NAME + " " + 
                    SQLDefinitions.ViewTable.IPAddress.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.HostName.NAME + " " +
                    SQLDefinitions.ViewTable.HostName.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.PortNumber.NAME + " " +
                    SQLDefinitions.ViewTable.PortNumber.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.UserName.NAME + " " + 
                    SQLDefinitions.ViewTable.UserName.TYPE + ",\n" +
                    SQLDefinitions.ViewTable.ViewTimestamp.NAME + " " + 
                    SQLDefinitions.ViewTable.ViewTimestamp.TYPE + ",\n" +
                    "PRIMARY KEY (" + SQLDefinitions.ViewTable.Id.NAME + ")) " +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE " + 
                    "utf8_unicode_ci;;";

        }

        /**
         * The insert data statements. 
         */
        public static final class Insert {

            /**
             * Inserts a new view into the database.
             */
            public static final String INSERT_VIEW = 
                    "INSERT INTO `" + SQLDefinitions.ViewTable.NAME + "` (" +
                    SQLDefinitions.ViewTable.IPAddress.NAME + ", " +
                    SQLDefinitions.ViewTable.HostName.NAME + ", " +
                    SQLDefinitions.ViewTable.PortNumber.NAME + ", " +
                    SQLDefinitions.ViewTable.ViewTimestamp.NAME + ") " +
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
                    "SELECT COUNT(*) FROM `" + SQLDefinitions.ViewTable.NAME +
                    "`;";

            /**
             * Returns the most recent view time.
             */
            public static final String GET_MOST_RECENT_VIEW_TIME = 
                    "SELECT MAX(" + 
                    SQLDefinitions.ViewTable.ViewTimestamp.NAME + ") FROM " +
                    SQLDefinitions.ViewTable.NAME + ";";
        }
    }
}
