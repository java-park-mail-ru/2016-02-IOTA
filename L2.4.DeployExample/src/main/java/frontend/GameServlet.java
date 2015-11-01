package frontend;

import base.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author v.chibrikov
 */
public class GameServlet extends HttpServlet {
    static final Logger LOGGER = LogManager.getLogger(GameServlet.class);

    private AuthService authService;

    public GameServlet(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {

        Map<String, Object> pageVariables = new HashMap<>();
        String name = request.getParameter("name");
        String safeName = name == null ? "NoName" : name;
        LOGGER.info("saving user: " + safeName);
        authService.saveUserName(request.getSession().getId(), safeName);
        pageVariables.put("myName", safeName);

        response.getWriter().println(PageGenerator.getPage("game.html", pageVariables));

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
