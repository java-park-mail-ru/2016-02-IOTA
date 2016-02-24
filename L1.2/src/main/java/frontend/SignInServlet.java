package frontend;

import com.google.gson.JsonObject;
import main.AccountService;
import main.UserProfile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author esin88
 */
public class SignInServlet extends HttpServlet {
    private AccountService accountService;

    public SignInServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final String name = request.getParameter("name");
        final String password = request.getParameter("password");

        final JsonObject answer = new JsonObject();

        final UserProfile profile = accountService.getUser(name);
        if (profile != null && profile.getPassword().equals(password)) {
            response.setStatus(HttpServletResponse.SC_OK);
            answer.addProperty("Status", "Ok");
            answer.addProperty("Name", name);
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            answer.addProperty("Status", "Wrong login/password");
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(answer.toString());
    }
}
