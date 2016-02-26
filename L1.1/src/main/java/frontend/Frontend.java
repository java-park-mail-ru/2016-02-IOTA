package frontend;

import com.google.gson.JsonObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author esin88
 */
public class Frontend extends HttpServlet {

    private String lastLogin = "";

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final String login = request.getParameter("login");
        final JsonObject answer = new JsonObject();

        if(login == null || login.isEmpty()){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            answer.addProperty("lastLogin", lastLogin);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            answer.addProperty("currentLogin", login);
            lastLogin = login;
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(answer.toString());
    }
}
