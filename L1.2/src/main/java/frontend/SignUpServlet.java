package frontend;

import com.google.gson.JsonObject;
import main.AccountService;
import main.UserProfile;
import org.jetbrains.annotations.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author esin88
 */
public class SignUpServlet extends HttpServlet {
    private final AccountService accountService;

    public SignUpServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        final String name = request.getParameter("name");
        final String password = request.getParameter("password");

        final JsonObject answer = new JsonObject();

        if (!checkCredential(name)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            answer.addProperty("Status", "Name is empty");
        } else if (!checkCredential(password)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            answer.addProperty("Status", "Password is empty");
        } else if (accountService.addUser(name, new UserProfile(name, password))) {
            response.setStatus(HttpServletResponse.SC_OK);
            answer.addProperty("Status", "Ok");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            answer.addProperty("Status", "User exists");
        }

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(answer.toString());
    }

    private boolean checkCredential(@Nullable String credential) {
        return credential != null && !credential.isEmpty();
    }
}
