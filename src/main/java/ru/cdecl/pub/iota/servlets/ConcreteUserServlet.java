package ru.cdecl.pub.iota.servlets;

import co.paralleluniverse.fibers.Fiber;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.servlet.FiberHttpServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import org.jvnet.hk2.annotations.Service;
import ru.cdecl.pub.iota.exceptions.UserNotFoundException;
import ru.cdecl.pub.iota.models.UserProfile;
import ru.cdecl.pub.iota.services.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.NullPointerException;

@Service
@Singleton
@WebServlet(asyncSupported = true)
public final class ConcreteUserServlet extends FiberHttpServlet {

    @Inject
    AccountService accountService;

    @Override
    @Suspendable
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final Long userId = getUserIdFromHttpRequest(req);
        if (userId != null) {
            final UserProfile userProfile = accountService.getUserProfile(userId);

            final String email;
            final String login;
            try {
                email = userProfile.getEmail();
                login = userProfile.getLogin();
            } catch (NullPointerException npe) {
                System.out.println("No user with user id: " + userId);
                resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
                resp.getWriter().write(EMPTY_RESPONSE);
                return;
            }
            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("id", userId);
            jsonResponse.put("email", email);
            jsonResponse.put("login", login);

            resp.setStatus(RESP_STATUS_OK);
            resp.getWriter().write(jsonResponse.toString());
            return;
        }
        resp.setStatus(RESP_STATUS_NOT_AUTHORIZED);
        resp.getWriter().write(EMPTY_RESPONSE);
    }

    @Override
    @Suspendable
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            // todo
        } else {
            resp.setStatus(RESP_STATUS_FORBIDDEN);

        }
        //
    }

    @Override
    @Suspendable
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final HttpSession httpSession = req.getSession(false);
        final Long userIdFromHttpRequest = getUserIdFromHttpRequest(req);
        final Long userIdFromHttpSession = getUserIdFromHttpSession(httpSession);
        if (userIdFromHttpRequest != null && userIdFromHttpRequest.equals(userIdFromHttpSession)) {
            try {
                accountService.deleteUser(userIdFromHttpRequest);
            } catch (UserNotFoundException ignored) {
            }
        } else {
            resp.setStatus(RESP_STATUS_FORBIDDEN);
            // todo
        }
        //
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

    @Nullable
    private static Long getUserIdFromHttpSession(@Nullable HttpSession sess) {
        if (sess == null) {
            return null;
        }
        final Object userIdAttribute = sess.getAttribute("user_id");
        return (userIdAttribute instanceof Long)
                ? (Long) userIdAttribute
                : null;
    }

    private static final int RESP_STATUS_OK = 200;
    private static final int RESP_STATUS_NOT_AUTHORIZED = 401;
    private static final int RESP_STATUS_FORBIDDEN = 403;
    private static final int RESP_STATUS_SERVER_ERROR = 500;
    private static final String EMPTY_RESPONSE = "{}";
}
