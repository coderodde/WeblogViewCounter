package com.github.coderodde.weblog.viewcounter;

import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger ;
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
   
    private final Logger LOGGER = 
            Logger.getLogger(CountViewServlet.class.getName());
    
    @Override
    protected void doGet(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse) {
        try (PrintWriter printWriter = httpServletResponse.getWriter()) {
            printWriter.println(httpServletRequest.getRemoteAddr());
            printWriter.println(httpServletRequest.getRemoteHost());
            printWriter.println(httpServletRequest.getRemotePort());
            printWriter.println(httpServletRequest.getRemoteUser());
            printWriter.flush();
        } catch (Exception ex) {
            String message = ex.getMessage();
            String causeMessage = ex.getCause() != null ?
                    ex.getCause().getMessage() : 
                    "<unknown cause>";
            
            LOGGER.log(
                    Level.SEVERE, 
                    "Failure: {0}, caused by: {1}", 
                    new String[]{
                        message,
                        causeMessage,
                    }
            );
        }
    }
}
