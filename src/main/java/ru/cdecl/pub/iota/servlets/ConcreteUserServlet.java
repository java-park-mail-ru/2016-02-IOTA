package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Suspendable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.json.JSONWriter;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.UserAlreadyExistsException;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;
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
import java.lang.NullPointerException;
import java.util.Arrays;

@Service
@Singleton
@WebServlet(asyncSupported = true)
public final class ConcreteUserServlet extends JsonApiServlet {

    @Inject
    AccountService accountService;

    @Override
    @Suspendable
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final Long userId = getUserIdFromHttpRequest(req);
        if (userId != null) {
            final UserProfile userProfile = accountService.getUserProfile(userId);
            if (userProfile != null) {
                jsonWriter.key("id").value(userId);
                jsonWriter.key("email").value(userProfile.getEmail());
                jsonWriter.key("login").value(userProfile.getLogin());
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }

        jsonWriter.endObject();
    }

    @Override
    @Suspendable
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            final JSONObject jsonRequest = getJsonObjectFromHttpRequest(req);
            final UserProfile newUserProfile = new UserProfile(
                    userIdFromHttpRequest,
                    jsonRequest.getString("login"),
                    jsonRequest.getString("email")
            );
            final char[] newUserPassword = jsonRequest.getString("password").trim().toCharArray();
            // todo: validation
            try {
                accountService.editUser(userIdFromHttpRequest, newUserProfile, newUserPassword);
                jsonWriter.key("id").value(userIdFromHttpRequest);
            } catch (UserNotFoundException | UserAlreadyExistsException e) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } finally {
                Arrays.fill(newUserPassword, '\0');
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonWriter.key("status").value(resp.getStatus());
            jsonWriter.key("reason").value("Trying to edit another user.");
        }

        jsonWriter.endObject();
    }

    @Override
    @Suspendable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final JSONWriter jsonWriter = getJsonWriterForHttpResponse(resp);
        jsonWriter.object();

        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            try {
                accountService.deleteUser(userIdFromHttpRequest);
            } catch (UserNotFoundException ignored) {
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            jsonWriter.key("status").value(resp.getStatus());
            jsonWriter.key("reason").value("Trying to delete another user.");
        }

        jsonWriter.endObject();
    }

    @Nullable
    private static Long getUserIdFromHttpRequest(@NotNull HttpServletRequest req) {
        final String requestUri = req.getRequestURI();
        try {
            return Long.parseLong(requestUri.substring(requestUri.lastIndexOf('/') + 1));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return null;
        }
    }

}
