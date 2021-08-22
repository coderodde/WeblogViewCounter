package com.github.coderodde.weblog.viewcounter;

/**
 * This POJO class type defines a simple object for reporting to the front-end.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 22, 2021)
 * @since 1.6 (Aug 22, 2021)
 */
public final class JSONResponseObject {
    
    public boolean succeeeded;
    public int numberOfViews;
    
    public static void main(String[] args) {
        System.out.println(System.getenv("CLEARDB_DATABASE_URL"));
        System.out.println(SQLStatements.MainTable.Create.CREATE_MAIN_TABLE);
    }
}
