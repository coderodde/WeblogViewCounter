package com.github.coderodde.weblog.viewcounter;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
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
    
    @Override
    protected void doPost(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) 
    throws ServletException, IOException {
        DataAccessObject dataAccessObject = new DataAccessObject();
        JSONResponseObject jsonResponseObject;
        
        try {
            dataAccessObject.addView(httpServletRequest);
            jsonResponseObject = dataAccessObject.getViewCount();
        } catch (SQLException ex) {
            jsonResponseObject = new JSONResponseObject();
        }
        
        try (PrintWriter printWriter = httpServletResponse.getWriter()) {
            printWriter.print(GSON.toJson(jsonResponseObject));
        }
    }
}
