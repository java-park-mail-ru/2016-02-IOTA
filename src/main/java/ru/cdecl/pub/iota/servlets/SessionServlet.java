package ru.cdecl.pub.iota.servlets;

import org.json.JSONObject;
import org.json.JSONWriter;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.services.AccountService;
import ru.cdecl.pub.iota.servlets.base.JsonApiServlet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Service
@Singleton
@WebServlet
public class SessionServlet extends JsonApiServlet {

    @Inject
    AccountService accountService;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final Long userId = getUserIdFromHttpSession(req.getSession(false));
        if (userId != null && accountService.isUserExistent(userId)) {
            jsonWriter.key("id").value(userId);
        }

        jsonWriter.endObject();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession();
        final JSONObject jsonRequest = getJsonObjectFromHttpRequest(req);
        final Long userId = accountService.getUserId(jsonRequest.getString("login"));
        // todo: validation
        if (userId != null) {
            try {
                if (accountService.isUserPasswordCorrect(userId, jsonRequest.getString("password").trim().toCharArray())) {
                    httpSession.setAttribute("user_id", userId);
                    jsonWriter.key("id").value(userId);
                }
            } catch (UserNotFoundException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        jsonWriter.endObject();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession();
        httpSession.invalidate();

        jsonWriter.endObject();
    }

}
