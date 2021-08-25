package com.github.coderodde.weblog.viewcounter;

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
    public String mostRecentViewTime;
}
