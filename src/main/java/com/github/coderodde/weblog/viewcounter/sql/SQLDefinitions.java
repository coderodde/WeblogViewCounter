package com.github.coderodde.weblog.viewcounter.sql;

/**
 * This class defines the database table schemas.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 * @since 1.6 (Aug 22, 2021)
 */
public final class SQLDefinitions {

    /**
     * This class defines the structure of the main table.
     */
    public static final class ViewTable {

        /**
         * The name of the main table.
         */
        public static final String NAME = "view";

        /**
         * This class defines the ID column.
         */
        public static final class Id {
            public static final String NAME = "id";
            public static final String TYPE = "INT NOT NULL AUTO_INCREMENT";
        }

        /**
         * This class defines the IP address column.
         */
        public static final class IPAddress {
            // 16 chars, 15 colons:
            public static final int IPV6_ADDRESS_STRING_LENGTH = 16 + 15;
            public static final String NAME = "ip_address";
            public static final String TYPE = 
                    "VARCHAR(" + IPV6_ADDRESS_STRING_LENGTH + ") NOT NULL";
        }

        /**
         * This class defines the host name column.
         */
        public static final class HostName {
            public static final int MAXIMUM_LENGTH = 253;
            public static final String NAME = "host_name";
            public static final String TYPE =
                    "VARCHAR(" + MAXIMUM_LENGTH + ")"; 
        }

        /**
         * This class defines the port number column.
         */
        public static final class PortNumber {
            public static final String NAME = "port";
            public static final String TYPE = "INT NOT NULL";
        }

        public static final class UserName {
            public static final String NAME = "user_name";
            public static final String TYPE = "VARCHAR(256)";
        }

        /**
         * This class defines the view timestamp.
         */
        public static final class ViewTimestamp {
            public static final String NAME = "viewed_at";
            public static final String TYPE = "TIMESTAMP NOT NULL";
        }
    }
}
