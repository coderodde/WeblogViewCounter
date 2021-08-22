package com.github.coderodde.weblog.viewcounter;

import static com.github.coderodde.weblog.viewcounter.Utils.objs;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
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
public final class CountViewServlet extends HttpServlet {
    
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER =
            Logger.getLogger(CountViewServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) 
    throws ServletException, IOException {
        DataAccessObject dataAccessObject = DataAccessObject.getInstance();
        JSONResponseObject jsonResponseObject = null;
        
        try {
            dataAccessObject.createTablesIfNeeded();
            dataAccessObject.addView(httpServletRequest); 
            jsonResponseObject = dataAccessObject.getViewCount();
        } catch (SQLException ex) {
            jsonResponseObject = new JSONResponseObject();
        } catch (CannotCreateMainTableException ex) {
            LOGGER.log(
                    Level.SEVERE, 
                    "Could not create the main table: {0}, caused by: {1}", 
                    objs(ex.getMessage(), 
                         ex.getCause().getMessage()));
            
            jsonResponseObject = new JSONResponseObject();
        } catch (URISyntaxException ex) {
            
        }
        
        try (PrintWriter printWriter = httpServletResponse.getWriter()) {
            printWriter.print(GSON.toJson(jsonResponseObject));
        }
    }
}
