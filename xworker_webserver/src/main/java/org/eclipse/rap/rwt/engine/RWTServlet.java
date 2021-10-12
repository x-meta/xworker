package org.eclipse.rap.rwt.engine;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RWTServlet extends HttpServlet {
    private static final long serialVersionUID = -2344709395951463612L;

    public RWTServlet() {
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().println("It is not real rap, run rap please use dml-rap.cmd or dml-rap.sh start web server.");
    }
}
