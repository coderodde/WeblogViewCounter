package com.github.coderodde.weblog.viewcounter;

import java.time.ZonedDateTime;

/**
 * This POJO class type defines a simple object for reporting to the front-end.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 * @since 1.6 (Aug 22, 2021)
 */
public final class JSONResponseObject {
    
    public boolean succeeded;
    public int numberOfViews;
    public ZonedDateTime mostRecentViewTime;
    
    public static void main(String[] args) {
        System.out.println(System.getenv("CLEARDB_DATABASE_URL"));
        System.out.println(SQLStatements.ViewTable.Create.CREATE_MAIN_TABLE);
    }
}
