package com.github.coderodde.weblog.viewcounter;

import static com.github.coderodde.weblog.viewcounter.Util.objects;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is responsible for storing the IP-address and the timestamp of 
 * a view in at <a href="http://coderodde.github.io/weblog/">coderodde's weblog</a>.
 *
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Aug 21, 2021)
 * @since 1.6 (Aug 21, 2021)
 */
@WebServlet(name="CountViewServlet", urlPatterns={"/countView"})
public class CountViewServlet extends HttpServlet {

    private static final Gson GSON = new Gson();
    private static final Logger LOGGER =
            Logger.getLogger(CountViewServlet.class.getName());

    @Inject private DataAccessObject dataAccessObject;

    @Override
    protected void doPost(HttpServletRequest httpServletRequest,
                          HttpServletResponse httpServletResponse) 
    throws ServletException, IOException {
        // Allow the weblog page to get the response from this servlet:
        httpServletResponse.setHeader("Access-Control-Allow-Origin", 
                                      "coderodde.github.io");

        JSONResponseObject jsonResponseObject = new JSONResponseObject();
        jsonResponseObject.succeeded = false;

        try {
            String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            
            if (ipAddress == null) {
                ipAddress = httpServletRequest.getRemoteAddr();
            }
            
            dataAccessObject.createTablesIfNeeded();
            ZonedDateTime mostRecentViewTime = 
                    dataAccessObject.getMostRecentViewTime();

            if (mostRecentViewTime != null) {
                jsonResponseObject.mostRecentViewTime =
                        mostRecentViewTime.toString();
            }
            
            ZonedDateTime visitorsMostRecentViewTime =
                    dataAccessObject.getVisitorsMostRecentViewTime(
                            httpServletRequest.getRemoteAddr());

            if (visitorsMostRecentViewTime != null) {
                jsonResponseObject.visitorsMostRecentViewTime =
                        visitorsMostRecentViewTime.toString();
            }
            
            dataAccessObject.addView(httpServletRequest); 
            jsonResponseObject.numberOfTotalViews = 
                    dataAccessObject.getTotalViewCount();

            jsonResponseObject.numberOfVisitorsViews = 
                    dataAccessObject.getVisitorsViweCount(ipAddress);
            
            // Mark as successful:
            jsonResponseObject.succeeded = true;
        } catch (SQLException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "SQL failed: {0}, caused by: {1}", 
                    objects(ex.getMessage(), ex.getCause()));

        } catch (URISyntaxException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "Bad DB URI: {0}, caused by: {1}", 
                    objects(ex.getMessage(), ex.getCause()));
        }

        try (PrintWriter printWriter = httpServletResponse.getWriter()) {
            printWriter.print(GSON.toJson(jsonResponseObject));
        }
    }
}
